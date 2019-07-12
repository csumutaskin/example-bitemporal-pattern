package tr.com.poc.temporaldate.core.dao.impl;

import static tr.com.poc.temporaldate.core.util.DateUtils.END_OF_SOFTWARE;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.dao.annotation.PidDetail;
import tr.com.poc.temporaldate.core.dao.annotation.PidDetector;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.BaseEntity;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.util.comparator.SortBaseEntityByEffectiveStartDateComparator;

/**
 * Helper utility methods for data access operations on a specific {@link BaseEntity extended entity}
 * @author umutaskin
 *
 * @param <E>
 */
@Log4j2
@Component
@SuppressWarnings({"unchecked","rawtypes"})
public class BaseBiTemporalDaoHelperImpl<E extends BaseBitemporalEntity>
{
	private static final String EFFECTIVE_DATE_STR = "effectiveDate";
	private static final String PERSPECTIVE_DATE_STR = "perspectiveDate";
	private static final String SELECT_E_FROM_PREFIX = "SELECT E FROM ";
	private static final String SELECT_DISTINCT_E_FROM_PREFIX = "SELECT DISTINCT E FROM ";
	
	/**
 	* Reads next value from the given sequenceName from database 
 	* @param entityManager entityManager for SQL operations
 	* @param sequenceName name of the database sequence
 	* @return
 	*/
	public Object getSequenceNextValue(EntityManager entityManager, String sequenceName)
	{
		String sqlStr = "Select "+ sequenceName +".nextval from dual";
		Query nextSequenceValue = entityManager.createNativeQuery(sqlStr);
		return nextSequenceValue.getSingleResult();		
	}
	
	/**
	 * Retrieves entity for reading or with pessimistic lock in case of a further update using its primary key id (not the @Pid id)
	 * @param beType class type of the BaseEntity
	 * @param entityManager manager for database operations
	 * @param pk pid value
	 * @param lockModeType for database tuple locking
	 * @return entity returned from query
	 */
	public E getEntityWithPrimaryIdForUpdateWithLockMode(Class<?> beType, EntityManager entityManager, final Serializable pk, LockModeType lockModeType)
	{
		if(pk == null)
		{
			return null;
		}		
		Query selectCurrentDataUsingEffectiveTimeParameter = null;
		selectCurrentDataUsingEffectiveTimeParameter = entityManager.createQuery(SELECT_E_FROM_PREFIX + beType.getSimpleName() + " E WHERE E.effectiveDateStart <= sysdate and E.effectiveDateEnd > sysdate and sysdate >= perspectiveDateStart and sysdate < perspectiveDateEnd and E.id = :id");
		selectCurrentDataUsingEffectiveTimeParameter.setParameter(Constants.ID_COLUMN_KEY, pk);
		if(lockModeType != null)
		{
			selectCurrentDataUsingEffectiveTimeParameter.setLockMode(lockModeType);
		}
		return (E) selectCurrentDataUsingEffectiveTimeParameter.getSingleResult();		
	}
	
	/**
	 * Retrieves entity using @Pid for reading or with pessimistic lock in case of a further update
	 * @param beType class type of the BaseEntity
	 * @param entityManager manager for database operations
	 * @param pid pid value
	 * @param lockModeType for database tuple locking
	 * @param perspectiveDate perspective date of querying
	 * @param effectiveDate effective date to select tuples
	 * @return list of BaseEntities that match the criteria
	 */
	public List<E> getEntityWithNaturalIdForUpdateWithLockMode(Class<?> beType, EntityManager entityManager, final Serializable pid, LockModeType lockModeType, LocalDateTime perspectiveDate, LocalDateTime effectiveDate)
	{
		 PidDetail pidTypeAndName = PidDetector.getPidTypesAndNamesMap().get(beType);
		 Query query = queryGeneratorWithPidAndDates(entityManager, beType, lockModeType, pidTypeAndName, perspectiveDate, effectiveDate, pid);
		 return (List<E>) query.getResultList();		 
	}
	
	/**
	 * Validates Perspective Dates and Effective Dates of an entity to be persisted or to be updated
	 * All Dates Should be filled (non null) Effective Begin Date, Effective End Date, Perspective Begin Date, Perspective End Date
	 * Perspective Begin Date <= Perspective End Date
	 * Effective Begin Date <= Effective End Date
	 * For Only Save Operations: Perspective End Date = Infinity (End of Software: Year ~2100)
	 * @param beType class type of BaseEntity
	 * @param entityToBeChecked entity to be checked for its date operations
	 * @param type current database operation type {@link OperationType}
	 */
	public void validateDates(Class<?> beType, E entityToBeChecked, OperationType type)
	{
		String objectType = beType.getSimpleName();
		if(entityToBeChecked == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_CANNOT_BE_NULL, type.toString(), objectType);
		}
		String objectToString = entityToBeChecked.toString();
		if(entityToBeChecked.getEffectiveDateStart() == null || entityToBeChecked.getEffectiveDateEnd() == null || entityToBeChecked.getPerspectiveDateStart() == null || entityToBeChecked.getPerspectiveDateEnd() == null)
		{
			log.error("At least one of the existing begin, existing end, perspective begin, perspective end dates in a {} operation for object of type: {} is absend. Object toString() is: {}", type, objectType, objectToString);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_OR_UPDATED_ENTITIES_ALL_4_DATES_NOT_EXIST, type.toString(), objectType, objectToString);
		}
		if(entityToBeChecked.getPerspectiveDateEnd().isBefore(entityToBeChecked.getPerspectiveDateStart()))
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_PERSPECTIVE_END_BEFORE_PERSPECTIVE_BEGIN, type.toString(), beType.getSimpleName(), entityToBeChecked.toString());
		}
		if(entityToBeChecked.getEffectiveDateEnd().isBefore(entityToBeChecked.getEffectiveDateStart()))
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_EFFECTIVE_END_BEFORE_EFFECTIVE_BEGIN, type.toString(), beType.getSimpleName(), entityToBeChecked.toString());
		}	
		if(type == OperationType.SAVE && entityToBeChecked.getPerspectiveDateEnd().isBefore(END_OF_SOFTWARE))//Save operation before a perspective end date of end of software
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_PERSPECTIVE_END_BEFORE_END_OF_SOFTWARE, type.toString(), beType.getSimpleName(), entityToBeChecked.toString());
		}
		if(type == OperationType.SAVE && entityToBeChecked.getEffectiveDateEnd().isBefore(END_OF_SOFTWARE))//Save operation before an effective end date of end of software
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_ENTITY_EFFECTIVE_END_BEFORE_END_OF_SOFTWARE, type.toString(), beType.getSimpleName(), entityToBeChecked.toString());
		}
	}
	
	/**
	 * Internal method used for getEntityWithNaturalIdForUpdateWithLockMode()'s query parameter setting only 
	 * @param entityManager entity manager for database operations
	 * @param entityClazz class type of the entity
	 * @param lockModeType lock type of database querying (optimistic lock, no lock, pessimistic lock)
	 * @param pidDetail detail of the pid column in current entity
	 * @param perspectiveDate perspective date of querying
	 * @param effectiveDate effective date of the tuples being queried
	 * @param pid pid value of the &#64;pid column
	 * @return {@link Query} prepared
	 */
	public Query queryGeneratorWithPidAndDates(EntityManager entityManager, Class<?> entityClazz, LockModeType lockModeType, PidDetail pidDetail, LocalDateTime perspectiveDate, LocalDateTime effectiveDate, Serializable pid)
	{
		String entityClassName = entityClazz.getSimpleName();
		if(pidDetail == null || pidDetail.getType() == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_ENTITY_SELECTION_WITH_NO_PID_OR_NO_PID_TYPE, entityClassName); 
		}
		boolean perspectiveDatePresent =  perspectiveDate != null;
		boolean effectiveDatePresent = effectiveDate != null;
		boolean pidPresent =  pid != null;
		
		StringBuilder queryStr = new StringBuilder(SELECT_E_FROM_PREFIX + entityClassName + " E WHERE 1 = 1 ");
		if(pidPresent)
		{
			queryStr.append(" AND E."+ pidDetail.getName() +" = :pid");			
		}
		if(perspectiveDatePresent)
		{
			queryStr.append(" AND :perspectiveDate >= perspectiveDateStart and :perspectiveDate < perspectiveDateEnd");			
		}
		//or  query string  append and now gte perspective date start and now lt perspective date end 
		if(effectiveDatePresent)
		{
			queryStr.append(" AND E.effectiveDateStart <= :effectiveDate and E.effectiveDateEnd > :effectiveDate");
		}
		//or query string append and effective date start lte now and effective date end gt now		
		Query query = entityManager.createQuery(queryStr.toString());
		if(pidPresent)
		{			
			query.setParameter("pid", pidDetail.castGivenValueToPidType(entityClazz, pid));
		}
		if(perspectiveDatePresent)
		{
			query.setParameter(PERSPECTIVE_DATE_STR, perspectiveDate);
		}
		if(effectiveDatePresent)
		{
			query.setParameter(EFFECTIVE_DATE_STR, effectiveDate);
		}
		if(lockModeType != null)
		{
			query.setLockMode(lockModeType);
		}
		return query;
	}
	
	/**
	 * Returns &#64;pid column details of current entity for a particular type of operation 
	 * (OperationType is for logging purposes only - if not indicated by the developer, takes the value UNDEFINED)
	 * @param beType class type of the current entity
	 * @param type operation type {@link OperationType}
	 * @return {@link PidDetail}
	 */
	public PidDetail getPidInfoOfCurrentEntity(Class<?> beType, OperationType type)
	{
		if(beType == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_ENTITY_CLASS_CAN_NOT_BE_NULL);
		}
		Map<Class<?>, PidDetail> pidTypesAndNamesMap = PidDetector.getPidTypesAndNamesMap();
		PidDetail toReturn = pidTypesAndNamesMap.get(beType);
		if(toReturn == null)
		{
			String daoOperationType = type == null ? OperationType.UNDEFINED.toString(): type.toString();
			String entityName = beType.getSimpleName();
			log.error("On a {} operation of a Base BiTemporal Entity with @Pid utility, no @Pid is found on any fields of {} entity, no save will be made. ", daoOperationType, entityName);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_ENTITY_HAS_NO_PID_COLUMN_THAT_CAN_BE_USED_IN_PID_UTILITY, daoOperationType, entityName);
		}	
		if(toReturn.getField() != null)
		{
			toReturn.getField().setAccessible(true);
		}
		return toReturn;
	}
	
	/**
	 * Returns the relevant converter to convert current BaseEntity to BaseDTO and vice versa
	 * @param baseConverter converter class to be used in conversion process
	 * @return {@link BaseConverter}
	 */
	public <D extends BaseBitemporalDTO> BaseConverter<E,D> getRelevantConverter(Class<? extends BaseConverter<E,D>> baseConverter)
	{
		if(baseConverter == null)
		{
			String errorMessage = "Can not instantiate a user given null base converter object in repository layer. Detail: A null object is given as parameter to the method";	
			log.info("Can not instantiate a user given null base converter object in repository layer. See error log for details.");
			log.error(errorMessage);
			throw new ApplicationException(ExceptionConstants.GET_RELEVANT_CONVERTER_EXCEPTION, new NullPointerException());
		}
		try 
		{
			Class<?> clazz = Class.forName(baseConverter.getName());
			return (BaseConverter<E,D>)clazz.getDeclaredConstructor().newInstance();
		} 
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException| NoSuchMethodException | SecurityException | ClassNotFoundException e) 
		{		
			String errorMessage = "Can not instantiate/or locate an existing base converter object in repository layer. Detail: " + ExceptionUtils.getStackTrace(e);	
			log.info("Can not instantiate/or locate an existing base converter object in repository layer. See error log for details.");
			log.error(errorMessage);
			throw new ApplicationException(ExceptionConstants.GET_RELEVANT_CONVERTER_EXCEPTION, e);
		}		
	}
	
	/**
	 * Retrieves all entities that is active between the given effective start and end dates, at now perspective time
	 * @param beType class type of the BaseEntity
	 * @param entityManager entityManager for database operations
	 * @param pid pid value to be queried
	 * @param effectiveStartDate start date inclusive of the search interval 
	 * @param effectiveEndDate end date (exclusive) of the search interval
	 * @param pidDetail column detail about the pid colum 
	 * @return Collection of {@link BaseEntity} object that match the given criteria, if none matches, an empty array list is returned
	 */
	Collection<E> getAllEntitiesWithinDates(Class<?> beType, EntityManager entityManager, final Serializable pid, LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate, PidDetail pidDetail)
	{
		Collection<E> toReturnCollection = new ArrayList<>();
		if(pid == null)
		{
			return toReturnCollection;
		}	
		
		StringBuilder queryStr = new StringBuilder(SELECT_DISTINCT_E_FROM_PREFIX).append(beType.getSimpleName()).append(" E WHERE E.").append(pidDetail.getName())
		.append(" = :pid  AND :perspectiveDate >= E.perspectiveDateStart and :perspectiveDate < E.perspectiveDateEnd AND (") 
		.append("(E.effectiveDateStart <= :effectiveStartDate AND E.effectiveDateEnd >= :effectiveStartDate) OR ")
		.append("(E.effectiveDateStart > :effectiveStartDate AND E.effectiveDateEnd < :effectiveEndDate) OR ")
		.append("(E.effectiveDateStart <= :effectiveEndDate AND E.effectiveDateEnd >= :effectiveEndDate)").append(")");
		log.info("Query Created: {}", queryStr);

		Query selectWithinEffectiveTimeFromPerspectiveDate = entityManager.createQuery(queryStr.toString());
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveStartDate", effectiveStartDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveEndDate", effectiveEndDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter(PERSPECTIVE_DATE_STR, LocalDateTime.now());
		Object pidCasted = pidDetail.castGivenValueToPidType(beType, pid);		
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("pid", pidCasted);
		try
		{
			toReturnCollection = (List<E>) selectWithinEffectiveTimeFromPerspectiveDate.getResultList();		
		}
		catch(Exception e)
		{
			String classTypeStr = beType.getSimpleName();
			String errorMessage = "Can not retrieve all tuples of type " + classTypeStr +"  withing given effective date range with a begin date of: " + effectiveStartDate + " and an end date of: " + effectiveEndDate + ". Detail: " + ExceptionUtils.getStackTrace(e);
			log.error(errorMessage);							 
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ALL_ENTITIES_THAT_INTERSECT_BEGIN_AND_END_DATE, e, classTypeStr, effectiveStartDate.toString(), effectiveEndDate.toString());	
		}
		if(CollectionUtils.isEmpty(toReturnCollection))
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_UPDATE_PID_UTILITY_CAN_NOT_DETECT_ANY_PRIOR_TUPLES_TO_UPDATE, pidDetail.getName(), pid.toString(), beType.getSimpleName());
		}
		return toReturnCollection;
	}
    
    /**
     * Checks if any gaps exist WRT effective dates
     * @param entityManager entity manager to be queried
     * @param beType class type of the base entity
     * @param pidDetail pid column of the current base entity
     * @param pid value of the pid column
     * @param gapPossibleStartDate start date inclusive to search interval
     * @param gapPossibleEndDate end date exclusive of the search interval
     * @return true if no gap exists, false if gap exists
     */
	//TODO: implement
	public boolean checkNoEffectiveDateGapsAfterUpdate(EntityManager entityManager, Class<?> beType, PidDetail pidDetail, Serializable pid, LocalDateTime gapPossibleStartDate, LocalDateTime gapPossibleEndDate)//TODO: Get rid of all extra queries
	{
		return true;
	}
	
    /**
     * Checks if any gaps exist WRT perspective dates
     * @param entityManager entity manager to be queried
     * @param beType class type of the base entity
     * @param pidDetail pid column of the current base entity
     * @param pid value of the pid column
     * @param gapPossibleStartDate start date inclusive to search interval
     * @param gapPossibleEndDate end date exclusive of the search interval
     * @return true if no gap exists, false if gap exists
     */
	//TODO: check...
	public boolean checkNoPerspectiveDateGapsAfterUpdate(EntityManager entityManager, Class<?> beType, PidDetail pidDetail, Serializable pid, LocalDateTime gapPossibleStartDate, LocalDateTime gapPossibleEndDate)//TODO: Get rid of all extra queries
	{
		if(pid == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTE_OR_UPDATE_GAP_CONTROL_PID_CAN_NOT_BE_NULL, beType.getSimpleName());
		}
		//select max effective end date which is closest to the newly inserted tuple's effective begin date, to check if there is a gap before the effective begin date of the new updated tuple
		String queryToCheckBeginDate = "Select max(E.perspectiveDateEnd) from "+ beType.getSimpleName() +" E where E."+ pidDetail.getName() +" = :pid and E.perspectiveDateEnd <= :gapPossibleStartDate";
		Query checkBeginQuery = entityManager.createQuery(queryToCheckBeginDate);
		checkBeginQuery.setParameter("pid", pidDetail.castGivenValueToPidType(beType, pid));
		checkBeginQuery.setParameter("gapPossibleStartDate", gapPossibleStartDate);
		List<E> checkBeginList = checkBeginQuery.getResultList();
		if(!CollectionUtils.isEmpty(checkBeginList))
		{
			E maxEndDateOnTable = checkBeginList.get(0);
			if(maxEndDateOnTable.getEffectiveDateEnd().isBefore(gapPossibleStartDate))
			{
				throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTE_OR_UPDATE_GAP_EXISTS_BEFORE_EFFECTIVE_START_DATE, beType.getSimpleName(),gapPossibleStartDate.toString());			
			}
		}
		// select minimum effective start date which is closest to the newly inserted tuple's effective end date, to check if there is a gap after the effective end date of the new updated tuple
		// if new end date is end of software no need to make any checks
		if(gapPossibleEndDate.isBefore(END_OF_SOFTWARE))
		{
			String queryToSelectEndDate = "Select min(E.perspectiveDateStart) from "+ beType.getSimpleName() +" E, where E."+ pidDetail.getName() +" = :pid and E.perspectiveDateBegin >= :gapPossibleEndDate";
			Query checkEndQuery = entityManager.createQuery(queryToSelectEndDate);
			checkEndQuery.setParameter("pid", pidDetail.castGivenValueToPidType(beType, pid));
			checkEndQuery.setParameter("gapPossibleEndDate", gapPossibleEndDate);
			List<E> checkEndList = checkEndQuery.getResultList();
			if(!CollectionUtils.isEmpty(checkEndList))
			{
				E minEndDateOnTable = checkEndList.get(0);
				if(minEndDateOnTable.getEffectiveDateStart().isAfter(gapPossibleEndDate))
				{
					throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTE_OR_UPDATE_GAP_EXISTS_AFTER_EFFECTIVE_END_DATE, beType.getSimpleName(),gapPossibleStartDate.toString());		
				}
			}	
		}		
		return true;
	}
	
	/**
	 * Persists a new tuple that is cloned from the oldTupleToBeOverridden with dates overridden to cloned data 
	 * @param entityManager entity manager for database operations
	 * @param parentClazzType clazz type of the BaseEntity in operations
	 * @param oldTupleToBeOverridden original data to be cloned and persisted (after date values are overridden to old date values)
	 * @param newEffectiveBeginDate new effective begin date to be applied to the cloned data before persisted (if null, old value from the clone will be persisted with the new entity)
	 * @param newEffectiveEndDate new effective end date to be applied to the cloned data before persisted (if null, old value from the clone will be persisted with the new entity)
	 * @param newPerspectiveBeginDate new perspective begin date to be applied to the cloned data before persisted (if null, old value from the clone will be persisted with the new entity)
	 * @param newPerspectiveEndDate new perspective begin date to be applied to the cloned data before persisted (if null, old value from the clone will be persisted with the new entity)
	 */
	public void persistData(EntityManager entityManager, Class<?> parentClazzType, E oldTupleToBeOverridden, LocalDateTime newEffectiveBeginDate, LocalDateTime newEffectiveEndDate, LocalDateTime newPerspectiveBeginDate, LocalDateTime newPerspectiveEndDate)
	{		
		E toInsert;
		Field declaredIdField;
		try 
		{
			toInsert = (E)oldTupleToBeOverridden.clone();
			declaredIdField= parentClazzType.getDeclaredField(Constants.ID_COLUMN_KEY);
			declaredIdField.setAccessible(true);
			declaredIdField.set(toInsert, null);
		}	 
		catch (CloneNotSupportedException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
		{
			throw new ApplicationException(ExceptionConstants.CLONING_ENTITY_EXCEPTION, e, parentClazzType.getSimpleName());					
		}
		if(newEffectiveBeginDate != null)
		{
			toInsert.setEffectiveDateStart(newEffectiveBeginDate);
		}
		if(newEffectiveEndDate != null)
		{
			toInsert.setEffectiveDateEnd(newEffectiveEndDate);
		}
		if(newPerspectiveBeginDate != null)
		{
			toInsert.setPerspectiveDateStart(newPerspectiveBeginDate);
		}
		if(newPerspectiveEndDate != null)
		{
			toInsert.setPerspectiveDateEnd(newPerspectiveEndDate);
		}
		if(toInsert.getEffectiveDateStart().isBefore(toInsert.getEffectiveDateEnd()))//Detect ederken buyuk esit ve kucuk esit almistim, yazmaya degecek bir araligi varsa kaydet.. 
		{
			entityManager.persist(toInsert);
		}	
	}
	
	/**
	 * In case of an update operation, this method detects what will happen to the old tuples which overlaps with the new updated data on effective date time line.
	 * @param entityManager entity manager for database operations
	 * @param parentClazzType class type of the BaseEntity in operations
	 * @param allEntitiesToUpdate all entities that overlaps the new updated data on effective time line
	 * @param newPersectiveBeginDate new perspective begin date, that is to be used, where necessary
	 * @param newEffectiveBeginDate new effective begin date, that is to be used, where necessary
	 * @param newEffectiveEndDate new effective end date, that is to be used, where necessary
	 */
	public void updateOldTuplesWithNewDates(EntityManager entityManager, Class<?> parentClazzType, List<E> allEntitiesToUpdate, LocalDateTime newPersectiveBeginDate, LocalDateTime newEffectiveBeginDate, LocalDateTime newEffectiveEndDate)
	{
		if(CollectionUtils.isEmpty(allEntitiesToUpdate))
		{
			return;
		}
		Comparator sortByEffectiveStartDateComparator = new SortBaseEntityByEffectiveStartDateComparator<>();
		Collections.sort((List<E>)allEntitiesToUpdate, sortByEffectiveStartDateComparator);		
		Iterator<E> iterator = allEntitiesToUpdate.iterator();
		while(iterator.hasNext())
		{			
			E next = iterator.next();
			if(!next.getEffectiveDateStart().isAfter(newEffectiveBeginDate) && !next.getEffectiveDateEnd().isBefore(newEffectiveBeginDate))//Type 1 and Type 2 or Type 1,6,5 (old tuple outer covering new one)
			{
				persistData(entityManager, parentClazzType, next, null, newEffectiveBeginDate, null, END_OF_SOFTWARE);//insert type 1				
				if(!next.getEffectiveDateEnd().isBefore(newEffectiveEndDate))//type 1,6,5 object -- 1 already inserted
				{					
					persistData(entityManager, parentClazzType, next, newEffectiveEndDate, null, null, END_OF_SOFTWARE);//insert type 5				
															
					//update type 6
					next.setEffectiveDateStart(newEffectiveBeginDate);
					next.setEffectiveDateEnd(newEffectiveEndDate);
					next.setPerspectiveDateEnd(newPersectiveBeginDate);					
				}
				else
				{
					//Update type2 object
					next.setEffectiveDateStart(newEffectiveBeginDate);
					next.setPerspectiveDateEnd(newPersectiveBeginDate);
				}
			}
			else if(next.getEffectiveDateStart().isAfter(newEffectiveBeginDate) && next.getEffectiveDateEnd().isBefore(newEffectiveEndDate))//Type 3
			{
				//Update type3 object
				next.setPerspectiveDateEnd(newPersectiveBeginDate);
			}
			else if(!next.getEffectiveDateStart().isAfter(newEffectiveEndDate) && !next.getEffectiveDateEnd().isBefore(newEffectiveEndDate))//Type 4 and Type 5
			{	
				//Insert type5 object
				persistData(entityManager, parentClazzType, next, newEffectiveEndDate, null, null, END_OF_SOFTWARE);//insert type 5
				//Update type4 object
				next.setEffectiveDateEnd(newEffectiveEndDate);
				next.setPerspectiveDateEnd(newPersectiveBeginDate);				
			}			
		}		
	}
}
