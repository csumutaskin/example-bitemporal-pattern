package tr.com.poc.temporaldate.core.dao.impl;

import static tr.com.poc.temporaldate.common.Constants.ID_SETTER_KEY;
import static tr.com.poc.temporaldate.core.util.DateUtils.END_OF_SOFTWARE;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
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
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.model.temporal.BaseTemporalEntity;
import tr.com.poc.temporaldate.core.util.comparator.SortBaseEntityByEffectiveStartDateComparator;

/**
 * Base CRUD operations on a Bitemporal entity
 * 
 * @author umutaskin
 *
 * @param <E> any entity that extends {@link BaseBitemporalEntity}
 */
@Component
@SuppressWarnings(value = { "rawtypes", "unchecked"})
@Log4j2
//TODO: Append isDeleted = 0 condition to all queries...
public class BaseBiTemporalDaoImpl<E extends BaseBitemporalEntity>
{
	private static final String EFFECTIVE_DATE_STR = "effectiveDate";
	private static final String PERSPECTIVE_DATE_STR = "perspectiveDate";
	private static final String SELECT_E_FROM_PREFIX = "SELECT E FROM ";
	private static final String SELECT_DISTINCT_E_FROM_PREFIX = "SELECT DISTINCT E FROM ";

	private enum OperationType {SAVE, UPDATE, READ, DELETE, UNDEFINED}
	
	@PersistenceContext
	private EntityManager entityManager;

	/*
	 * Returns the next value of the given sequence name
	 */
	private Object getSequenceNextValue(String sequenceName)
	{
		String sqlStr = "Select "+ sequenceName +".nextval from dual";
		Query nextSequenceValue = entityManager.createNativeQuery(sqlStr);
		return nextSequenceValue.getSingleResult();		
	}
	
	/**
	 * Returns the entity manager object that can be used directly in sub data access object classes
	 * @return {@link EntityManager}
	 */
	public EntityManager getEntityManager()
	{
		return entityManager;
	}

	/**
	 * Entity Manager setter
	 * @param entityManager entityManager
	 */
	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
	
	/**
	 * Retrieves currently effective entity, from current perspective using primary key id
	 * @param pk primary key to be searched (not the @Pid)
	 * @return {@link BaseTemporalEntity} 
	 */
	public E getEntityWithPrimaryId(final Serializable pk)
	{		
		return getEntityWithPrimaryIdForUpdateWithLockMode(pk, null);				
	}
	
	/**
	 * Retrieves an entity list (or a single entity but in an ArrayList Collection) that match the given date criteria
	 * @param pid natural key -which is marked with @Pid annotation- to be searched among the given entity. 
	 * </br>Natural key is normally the id of the entity, but this time it can be a repeatable item in a database table
	 * @param perspectiveDate if null get all entities that fit the given effectiveDate with given natural id, if a valid date given, returns the tuples that are valid at that perspective date  
	 * @param effectiveDate if null get all entities that fit the given perspective Date with given natural id, if a valid date given, returns the tuples that are valid at that effective date 
	 * @return {@link List of BaseTemporalEntity} object collection that match the given criteria
	 */
	public List<E> getEntityWithNaturalIdAtGivenDates(final Serializable pid, LocalDateTime perspectiveDate, LocalDateTime effectiveDate)
	{		
		return getEntityWithNaturalIdForUpdateWithLockMode(pid, null, perspectiveDate, effectiveDate);				
	}
	
	/**
	 * Retrieves an entity list (or a single entity but in an ArrayList Collection) that match the given date criteria with pessimistic lock, for update operations
	 * @param pid natural key -which is marked with @Pid annotation- to be searched among the given entity. 
	 * Natural key is normally the id of the entity, but this time it can be a repeatable tuple in a database table
	 * @param perspectiveDate, if null get all entities that fit the given effectiveDate with given natural id, if a valid date given, returns the tuples that are valid at that perspective date  
	 * @param effectiveDate, if null get all entities that fit the given perspective Date with given natural id, if a valid date given, returns the tuples that are valid at that effective date 
	 * @return {@link List of BaseTemporalEntity} object
	 * 
	 */
	public List<E> getEntityForUpdateWithNaturalIdAtGivenDates(final Serializable pid, LocalDateTime perspectiveDate, LocalDateTime effectiveDate)
	{		
		return getEntityWithNaturalIdForUpdateWithLockMode(pid, LockModeType.PESSIMISTIC_WRITE, perspectiveDate, effectiveDate);				
	}
	
	/* 
	 * Retrieves entity for reading or with pessimistic lock in case of a further update using its primary key id (not the @Pid id)
	 */
	private E getEntityWithPrimaryIdForUpdateWithLockMode(final Serializable pk, LockModeType lockModeType)
	{
		if(pk == null)
		{
			return null;
		}
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		Query selectCurrentDataUsingEffectiveTimeParameter = null;
		selectCurrentDataUsingEffectiveTimeParameter = entityManager.createQuery(SELECT_E_FROM_PREFIX + beType.getSimpleName() + " E WHERE E.effectiveDateStart <= sysdate and E.effectiveDateEnd > sysdate and sysdate >= recordDateStart and sysdate < recordDateEnd and E.id = :id");
		selectCurrentDataUsingEffectiveTimeParameter.setParameter(Constants.ID_COLUMN_KEY, pk);
		if(lockModeType != null)
		{
			selectCurrentDataUsingEffectiveTimeParameter.setLockMode(lockModeType);
		}
		return (E) selectCurrentDataUsingEffectiveTimeParameter.getSingleResult();		
	}
	
	/* 
	 * Retrieves entity using @Pid for reading or with pessimistic lock in case of a further update 
	 */	
	private List<E> getEntityWithNaturalIdForUpdateWithLockMode(final Serializable pid, LockModeType lockModeType, LocalDateTime perspectiveDate, LocalDateTime effectiveDate)
	{
		 Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		 
		 if(pid == null)
		 {
			 return new ArrayList<>();
		 }
		 PidDetail pidTypeAndName = PidDetector.getPidTypesAndNamesMap().get(beType);
		 Query query = queryGeneratorWithPidAndDates(beType, lockModeType, pidTypeAndName, perspectiveDate, effectiveDate, pid);
		 return (List<E>) query.getResultList();		 
	}
	
	/*
	 * Validates Perspective Dates and Effective Dates of an entity to be persisted or to be updated
	 * All Dates Should be filled (non null) Effective Begin Date, Effective End Date, Perspective Begin Date, Perspective End Date
	 * Perspective Begin Date <= Perspective End Date
	 * Effective Begin Date <= Effective End Date
	 * For Only Save Operations: Perspective End Date = Infinity (End of Software: Year ~2100)
	 */
	private void validateDates(Class<?> beType, E entityToBeChecked, OperationType type)
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
	
	/* 
	 * Internal method used for getEntityWithNaturalIdForUpdateWithLockMode()'s query parameter setting only 
	 */
	private Query queryGeneratorWithPidAndDates(Class<?> entityClazz, LockModeType lockModeType, PidDetail pidDetail, LocalDateTime perspectiveDate, LocalDateTime effectiveDate, Serializable pid)
	{
		String entityClassName = entityClazz.getSimpleName();
		if(pidDetail == null || pidDetail.getType() == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_ENTITY_SELECTION_WITH_NO_PID_OR_NO_PID_TYPE, entityClassName); 
		}
		boolean perspectiveDatePresent =  perspectiveDate != null;
		boolean effectiveDatePresent = effectiveDate != null;
		
		StringBuilder queryStr = new StringBuilder(SELECT_E_FROM_PREFIX + entityClassName + " E WHERE E."+ pidDetail.getName() +" = :pid");
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
		Object castedSerializableId = pidDetail.castGivenValueToPidType(entityClazz, pid);
		if(castedSerializableId == null)
		{
			 log.error("...Where @Pid = null, for type: {} and value: {} that passed to the method.", pidDetail.getType().toString(), pid);
			 throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ALL_ENTITIES_WITH_NULL_PID, entityClassName); 
		}	
		query.setParameter("pid", castedSerializableId);
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
	 * Saves or updates a single entity using the given parameters
	 * @param pid @Pid natural id to be updated (if null data will be persisted)
	 * @param baseEntity entity to be saved or updated
	 * @param autoUpdateChildren if true foreign key constraints are also searched in an update process and they are updated as well
	 * @return {@link BaseTemporalEntity} that is saved or updated
	 */
	public E saveOrUpdateEntityWithNaturalId(Serializable pid, E baseBiTemporalEntity, boolean autoUpdateChildren)
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		if(pid == null)//persist
		{
			toReturn = saveEntityWithinEffectiveAndPerspectiveDates(beType, baseBiTemporalEntity);
		}
		else//update
		{			
			toReturn = updateEntityWithinEffectiveAndPerspectiveDates(beType, pid, baseBiTemporalEntity, autoUpdateChildren);
		}
		return toReturn;
	}	
	
	/**
	 * Persists an entity to its related table
	 * @param beType type of object to be persisted
	 * @param toSave object that is to be persisted
	 * @return {@link E extends BaseEntity}
	 */
	public E saveEntityWithinEffectiveAndPerspectiveDates(Class<?> beType, E toSave)
	{
		validateDates(beType, toSave, OperationType.SAVE);
		PidDetail pidDetail = getPidInfoOfCurrentEntity(beType, OperationType.SAVE);
		if(pidDetail.readPidColumnValue(beType, toSave) == null)//For a persist operation @Pid value should be null and read from its own sequence
		{			
			pidDetail.setPidColumnValue(beType, toSave, getSequenceNextValue(pidDetail.getSequenceName()));
			entityManager.persist(toSave);
			return toSave;
		}
		return null;
	}
	
	/*
	 * Returns @pid column details of current entity for a particular type of operation 
	 * (OperationType is for logging purposes only - if not indicated by the developer, takes the value UNDEFINED)
	 */
	private PidDetail getPidInfoOfCurrentEntity(Class<?> beType, OperationType type)
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
	
	public  E updateEntityWithinEffectiveAndPerspectiveDates(Class<?> beType, Serializable pid, E toUpdate, boolean autoUpdateChildren)
	{		
		validateDates(beType, toUpdate, OperationType.UPDATE);
		PidDetail pidDetail = getPidInfoOfCurrentEntity(beType, OperationType.UPDATE);
		pidDetail.setPidColumnValue(beType, toUpdate, pid);
		Collection<E> allEntitiesWithinDates = getAllEntitiesWithinDates(pid, toUpdate.getEffectiveDateStart(), toUpdate.getEffectiveDateEnd(), pidDetail);
		updateOldTuplesWithNewDates(beType, (List<E>)allEntitiesWithinDates, toUpdate.getPerspectiveDateStart(), toUpdate.getPerspectiveDateEnd(), toUpdate.getEffectiveDateStart(), toUpdate.getEffectiveDateEnd());
		entityManager.persist(toUpdate);
//		checkNoPerspectiveDateGapsAfterUpdate(beType, pidDetail, pid, toUpdate.getEffectiveDateStart(), toUpdate.getEffectiveDateEnd());
//		checkNoEffectiveDateGapsAfterUpdate(beType, pidDetail, pid, toUpdate.getEffectiveDateStart(), toUpdate.getEffectiveDateEnd());
		if(autoUpdateChildren)
		{
			updateAllPidsUsedAsForeignKey();
		}
		return toUpdate;
	}
	
	private boolean checkNoEffectiveDateGapsAfterUpdate(Class<?> beType, PidDetail pidDetail, Serializable pid, LocalDateTime gapPossibleStartDate, LocalDateTime gapPossibleEndDate)//TODO: Get rid of all extra queries
	{
		return true;
	}
	
	private boolean checkNoPerspectiveDateGapsAfterUpdate(Class<?> beType, PidDetail pidDetail, Serializable pid, LocalDateTime gapPossibleStartDate, LocalDateTime gapPossibleEndDate)//TODO: Get rid of all extra queries
	{
		if(pid == null)
		{
			throw new RuntimeException("asdfafadfa");
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
				throw new RuntimeException("Baslangic Tarihi tarafinda bosluk var...");
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
					throw new RuntimeException("Bitis Tarihi tarafinda bosluk var...");
				}
			}	
		}		
		return true;
	}
	
	private boolean updateAllPidsUsedAsForeignKey()
	{
		//implement
		return true;
	}
	
	private void updateOldTuplesWithNewDates(Class<?> parentClazzType, List<E> allEntitiesToUpdate, LocalDateTime newPersectiveBeginDate, LocalDateTime newPerspectiveEndDate, LocalDateTime newEffectiveBeginDate, LocalDateTime newEffectiveEndDate)
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
				//Here in this if, type 1 operation is always common..
				//Insert type1 object
				E toInsertForType1;
				Field declaredIdField;
				try 
				{
					toInsertForType1 = (E)next.clone();
					declaredIdField= parentClazzType.getDeclaredField(Constants.ID_COLUMN_KEY);
					declaredIdField.setAccessible(true);
					declaredIdField.set(toInsertForType1, null);
				}	 
				catch (CloneNotSupportedException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
				{
					throw new ApplicationException(ExceptionConstants.CLONING_ENTITY_EXCEPTION, e, parentClazzType.getSimpleName());					
				}
				toInsertForType1.setEffectiveDateEnd(newEffectiveBeginDate);
				toInsertForType1.setPerspectiveDateEnd(END_OF_SOFTWARE);
				if(toInsertForType1.getEffectiveDateStart().isBefore(toInsertForType1.getEffectiveDateEnd()))//Detect ederken buyuk esit ve kucuk esit almistim, yazmaya degecek bir araligi varsa kaydet.. 
				{
					entityManager.persist(toInsertForType1);
				}	
				
				
				if(!next.getEffectiveDateEnd().isBefore(newEffectiveEndDate))//type 1,6,5 object -- 1 already inserted
				{
					//insert type 5
					E toInsertForType5;
					try
					{
						toInsertForType5 = (E)next.clone();		
						declaredIdField.set(toInsertForType5, null);
					} 
					catch (CloneNotSupportedException | SecurityException | IllegalArgumentException | IllegalAccessException  e) 
					{
						throw new ApplicationException(ExceptionConstants.CLONING_ENTITY_EXCEPTION, e, parentClazzType.getSimpleName());					
					}
					toInsertForType5.setEffectiveDateStart(newEffectiveEndDate);
					toInsertForType5.setPerspectiveDateEnd(END_OF_SOFTWARE);
					if(toInsertForType5.getEffectiveDateStart().isBefore(toInsertForType5.getEffectiveDateEnd()))//Detect ederken buyuk esit ve kucuk esit almistim, yazmaya degecek bir araligi varsa kaydet.. 
					{
						entityManager.persist(toInsertForType5);
					}
										
					//update type 6
					next.setEffectiveDateStart(newEffectiveBeginDate);
					next.setEffectiveDateEnd(newEffectiveEndDate);
					next.setPerspectiveDateEnd(newPersectiveBeginDate);					
				}
				else
				{
//					//Insert type1 object
//					E toInsertForType1;
//					Field declaredIdField;
//					try 
//					{
//						toInsertForType1 = (E)next.clone();
//						declaredIdField= parentClazzType.getDeclaredField(Constants.ID_COLUMN_KEY);
//						declaredIdField.setAccessible(true);
//						declaredIdField.set(toInsertForType1, null);
//					}	 
//					catch (CloneNotSupportedException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
//					{
//						throw new ApplicationException(ExceptionConstants.CLONING_ENTITY_EXCEPTION, e, parentClazzType.getSimpleName());					
//					}
//					toInsertForType1.setEffectiveDateEnd(newEffectiveBeginDate);
//					toInsertForType1.setPerspectiveDateEnd(END_OF_SOFTWARE);
//					if(toInsertForType1.getEffectiveDateStart().isBefore(toInsertForType1.getEffectiveDateEnd()))//Detect ederken buyuk esit ve kucuk esit almistim, yazmaya degecek bir araligi varsa kaydet.. 
//					{
//						entityManager.persist(toInsertForType1);
//					}	
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
				//Update type4 object
				next.setEffectiveDateEnd(newEffectiveEndDate);
				next.setPerspectiveDateEnd(newPersectiveBeginDate);
				//Insert type5 object
				E toInsertForType5;
				Field declaredIdField;
				try
				{
					toInsertForType5 = (E)next.clone();
					declaredIdField= parentClazzType.getDeclaredField(Constants.ID_COLUMN_KEY);
					declaredIdField.setAccessible(true);
					declaredIdField.set(toInsertForType5, null);
				} 
				catch (CloneNotSupportedException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) 
				{
					throw new ApplicationException(ExceptionConstants.CLONING_ENTITY_EXCEPTION, e, parentClazzType.getSimpleName());					
				}
				toInsertForType5.setEffectiveDateStart(newEffectiveEndDate);
				toInsertForType5.setPerspectiveDateEnd(END_OF_SOFTWARE);
				if(toInsertForType5.getEffectiveDateStart().isBefore(toInsertForType5.getEffectiveDateEnd()))//Detect ederken buyuk esit ve kucuk esit almistim, yazmaya degecek bir araligi varsa kaydet.. 
				{
					entityManager.persist(toInsertForType5);
				}
			}			
		}		
	}

	/*
	 * Retrieves all entities that intersect the given effective start and end dates
	 * @param pk primary key to be searched
	 * @return {@link BaseTemporalEntity} object
	 */	
   private Collection<E> getAllEntitiesWithinDates(final Serializable pid, LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate, PidDetail pidDetail)
	{
		Collection<E> toReturnCollection = new ArrayList<>();
		if(pid == null)
		{
			return toReturnCollection;
		}
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		
		StringBuilder queryStr = new StringBuilder(SELECT_DISTINCT_E_FROM_PREFIX).append(beType.getSimpleName()).append(" E WHERE E.").append(pidDetail.getName())
		.append(" = :pid  AND :perspectiveDate >= E.perspectiveDateStart and :perspectiveDate < E.perspectiveDateEnd AND (") 
		.append("(E.effectiveDateStart <= :effectiveStartDate AND E.effectiveDateEnd >= :effectiveStartDate) OR ")
		.append("(E.effectiveDateStart > :effectiveStartDate AND E.effectiveDateEnd < :effectiveEndDate) OR ")
		.append("(E.effectiveDateStart <= :effectiveEndDate AND E.effectiveDateEnd >= :effectiveEndDate)").append(")");
		log.info("Query Created: {}", queryStr);

		Query selectWithinEffectiveTimeFromPerspectiveDate = entityManager.createQuery(queryStr.toString());
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveStartDate", effectiveStartDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveEndDate", effectiveEndDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("perspectiveDate", LocalDateTime.now());
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
	 * Saves or updates an entity collection using the given parameters
	 * @param baseBiTemporalEntities entity collection to be saved or updated (if @Pid column are not null, update operation will be done, otherwise save operation will be done) 
	 * @param autoUpdateChildren if true foreign key constraints are also searched in an update process and they are updated as well
	 * @return {@link BaseTemporalEntity} that is saved or updated
	 */
	public E saveOrUpdatEntityCollectionWithNaturalId(Collection<E> baseBiTemporalEntities, boolean autoUpdateChildren)
	{
		//TODO: bulk implement
		return null;
	}
	
	/**
	 * Saves or updates entity from current perspective using DTO object
	 * @param id primary key to be updated (if null data will be persisted)
	 * @param baseEntity entity to be saved or updated
	 * @param effectiveStartDate actual start date where the tuple is active
	 * @param effectiveEndDate actual final date where the tuple is active
	 * @param mergeAndRearrangeDates if true no tuple having the same id is deleted, instead the given parameters withing given date range is merged with the older same id tuple in the timeline
	 * @return {@link BaseTemporalEntity} that is saved or updated
	 */
    public <D extends BaseBitemporalDTO> E saveorUpdateEntityByDTO(Serializable id, D updateDTO, Class<? extends BaseConverter<E,D>> baseConverter)
    {    	 
    	E baseEntity = getRelevantConverter(baseConverter).convertToEntity(updateDTO);
    	return saveOrUpdateEntityWithNaturalId(id, baseEntity, false);
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	
//	 /* Saves or Updates entity */
//	private E saveOrUpdateEntityWithFlushOption(Serializable pid, E baseEntity, boolean flushNeeded, LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate, boolean autoUpdateChildren)
//	{	
//		Collection<E> entitiesIntersected = getAllEntitiesThatIntersectBeginAndEndDate(pid, effectiveStartDate, effectiveEndDate);
//		if(CollectionUtils.isEmpty(entitiesIntersected))
//		{
//			entityManager.persist(enrichDateColumns(baseEntity,effectiveStartDate, effectiveEndDate));
//		}
//		else //update currently found entity
//		{
//			Collections.sort((List<E>)entitiesIntersected, new SortBaseEntityByEffectiveStartDateComparator<E>());
//			boolean endDateCoveredByAnyExistingTuple = false;
//			for(E current:entitiesIntersected)
//			{
//				//TODO: comparator sort by date begin, then start updating new dates using the given date by end user and also merge the new data 
//				//if both start and end dates match, update by merging new data
//				//if at least one date match, insert new data and limit the old data times..
//				if(DateUtils.dateBetweenDates(effectiveStartDate, current.getEffectiveDateStart(), current.getEffectiveDateEnd(), false))
//				{
//					current.setEffectiveDateEnd(effectiveStartDate);
//					continue;
//				}
//				if(DateUtils.dateBetweenDates(effectiveEndDate, current.getEffectiveDateStart(), current.getEffectiveDateEnd(), false))
//				{
//					endDateCoveredByAnyExistingTuple = true;					
//					current.setEffectiveDateStart(effectiveEndDate);
//				}
//			}
//			if(!endDateCoveredByAnyExistingTuple)
//			{
//				entityManager.persist(enrichDateColumns(baseEntity,effectiveStartDate, effectiveEndDate));
//			}
//		}
//		if(flushNeeded)
//		{
//			entityManager.flush();
//		}
//		return baseEntity;
//	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	/**
	 * Deletes entity which is effective at the given date
	 * @param id primary key to be updated (if null data will be persisted)
	 * @param effectiveDate actual date for the tuple
	 */
	public void deleteEntityAtEffectiveDate(Serializable id, Date effectiveDate)
	{
		//TODO: implement...
	}
	
	/**
	 * Deletes entity which is currently effective
	 * @param id primary key to be updated (if null data will be persisted)
	 */
	public void deleteEntityAtEffectiveDate(Serializable id)
	{
		//TODO: implement...
	}
	
	/**
	 * Deletes all versions????? of entity with the given id
	 * @param id primary key to be updated (if null data will be persisted)
	 */
	public boolean deleteEntityWithAllVersions(Serializable id)
	{
		//TODO: implement...
		return true;
	}
	
//	/**
//	 * Saves or updates entity from current perspective
//	 * @param id primary key to be updated (if null data will be persisted)
//	 * @param baseEntity entity to be saved or updated
//	 * @param effectiveStartDate actual start date where the tuple is active
//	 * @param effectiveEndDate actual final date where the tuple is active
// 	 * @param mergeAndRearrangeDates if true no tuple having the same id is deleted, instead the given parameters withing given date range is merged with the older same id tuple in the timeline
//	 * @return {@link BaseTemporalEntity} that is saved or updated
//	 */
//    //public E saveorUpdateEntity(Serializable id, E baseEntity, Date effectiveStartDate, Date effectiveEndDate)
//	public E saveorUpdateEntity(Serializable id, E baseEntity, LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate)
//    {
//    	return saveOrUpdateEntityWithFlushOption(id, baseEntity, false, effectiveStartDate, effectiveEndDate, false);
//    }
    
	/**
	 * Retrieves all entities that intersect the given effective start and end dates
	 * @param pk primary key to be searched
	 * @return {@link BaseTemporalEntity} object
	 */
	public <D extends BaseBitemporalDTO> List<D> getDTOListAtEffectiveDate(Class<? extends BaseConverter<E,D>> converterClass, Date effectiveDate)
	{
		List<E> allEntities = getEntityList(effectiveDate);
		return (List<D>) getRelevantConverter(converterClass).convertEntityCollectionToDTOCollection(allEntities);
	}
	
//	public <D extends BaseBitemporalDTO> D getDTOAtEffectiveDate(final Serializable pk, Class<? extends BaseConverter<E,D>> baseConverter, Date effectiveDate) 
//	{
//		return getRelevantConverter(baseConverter).convertToDTO(getEntityAtEffectiveTime(pk, effectiveDate));
//	}
	
	public List<E> getEntityList(Date effectiveDate)
	{
		return getEntityListWithLockMode(null, effectiveDate);
	}

	public List<E> getEntityListForUpdate(Date effectiveDate)
	{
		return getEntityListWithLockMode(LockModeType.PESSIMISTIC_WRITE, effectiveDate);		
	}
	
	private List<E> getEntityListWithLockMode(LockModeType lockModeType, Date effectiveDate)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		String query = "SELECT BE FROM " + beType.getSimpleName() + " BE WHERE :effectiveDate >= BE.effectiveDateStart AND :effectiveDate <= BE.effectiveDateEnd ";
		Query queryJpa = entityManager.createQuery(query);
		queryJpa.setParameter(EFFECTIVE_DATE_STR, effectiveDate);
		if(lockModeType != null)
		{
			queryJpa.setLockMode(lockModeType);
		}
		return queryJpa.getResultList();
	}
	   		
	@SuppressWarnings("unused")
	private E setIdusingReflection(E baseEntity, Serializable id) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
	{
		Method method = null;		
		try
		{
			Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			method = beType.getMethod(ID_SETTER_KEY, BigDecimal.class);
			method.invoke(baseEntity, id);
		}
		catch(Exception e)
		{
			String errorMessage = "Can not invoke setId() using reflection on object: " + baseEntity + ". Detail: " + ExceptionUtils.getStackTrace(e);
			log.error(errorMessage);
			log.info("Can not invoke setId() using reflection on object: " + baseEntity + " See errog log for details...");					 
			throw e;
		}
		return baseEntity;
	}
	
	/* Sets an effective (actual) start date and end date for the current tuple */
	private E enrichDateColumns(E baseEntity, LocalDateTime effectiveStartDate, LocalDateTime effectiveEndDate)
	{
		if(baseEntity.getPerspectiveDateStart() == null)
		{
			baseEntity.setPerspectiveDateStart(LocalDateTime.now());
		}
		if(baseEntity.getPerspectiveDateEnd() == null)
		{
			baseEntity.setPerspectiveDateEnd(END_OF_SOFTWARE);
		}
		if(baseEntity.getEffectiveDateStart() == null)
		{
			baseEntity.setEffectiveDateStart(effectiveStartDate);
		}
		if(baseEntity.getEffectiveDateEnd() == null)
		{
			baseEntity.setEffectiveDateEnd(effectiveEndDate);
		}
		return baseEntity;
	}
	
	private <D extends BaseBitemporalDTO> BaseConverter<E,D> getRelevantConverter(Class<? extends BaseConverter<E,D>> baseConverter)
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
			Class clazz = Class.forName(baseConverter.getName());
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
	
//	private void updateOldTuplesWithNewDates(List<E> allEntitiesToUpdate, LocalDateTime newPersectiveBeginDate, LocalDateTime newPerspectiveEndDate, LocalDateTime newEffectiveBeginDate, LocalDateTime newEffectiveEndDate)
//	{
//		if(CollectionUtils.isEmpty(allEntitiesToUpdate))
//		{
//			return;
//		}
//		Comparator sortByEffectiveStartDateComparator = new SortBaseEntityByEffectiveStartDateComparator<>();
//		Collections.sort((List<E>)allEntitiesToUpdate, sortByEffectiveStartDateComparator);		
//		Iterator<E> iterator = allEntitiesToUpdate.iterator();
//		while(iterator.hasNext())
//		{
//			E next = iterator.next();
//			if(!next.getEffectiveDateStart().isAfter(newEffectiveBeginDate) && !next.getEffectiveDateEnd().isBefore(newEffectiveBeginDate))//Type 1 and Type 2
//			{
//				//Insert type1 object
//				E toInsertForType1 = initializeObject();
//				//Update type2 object
//				next.setEffectiveDateStart(newEffectiveBeginDate);
//				next.setPerspectiveDateEnd(newPersectiveBeginDate);
//			}
//			else if(next.getEffectiveDateStart().isAfter(newEffectiveBeginDate) && next.getEffectiveDateEnd().isBefore(newEffectiveEndDate))//Type 3
//			{
//				//Update type3 object
//				next.setPerspectiveDateEnd(newPersectiveBeginDate);
//			}
//			else if(!next.getEffectiveDateStart().isAfter(newEffectiveEndDate) && !next.getEffectiveDateEnd().isBefore(newEffectiveEndDate))//Type 4 and Type 5
//			{
//				//Update type4 object
//				next.setEffectiveDateEnd(newEffectiveEndDate);
//				next.setPerspectiveDateEnd(newPersectiveBeginDate);
//				//Insert type5 object
//				E toInsertForType5 = initializeObject();
//			}			
//			
//			
//			
////			E next = iterator.next();
////			if(!next.getPerspectiveDateStart().isAfter(newPersectiveBeginDate) && (!next.getPerspectiveDateEnd().isBefore(newPersectiveBeginDate)))//Type 1
////			{
////				next.setPerspectiveDateEnd(newPersectiveBeginDate);
////			}
////			else if(next.getPerspectiveDateStart().isAfter(newPersectiveBeginDate) && (next.getPerspectiveDateEnd().isBefore(newPerspectiveEndDate)))//Type 2
////			{
////				next.setIsDeleted(Boolean.TRUE);
////			}
////			else if(!next.getPerspectiveDateStart().isAfter(newPerspectiveEndDate) && (!next.getPerspectiveDateEnd().isBefore(newPerspectiveEndDate)))//Type 3
////			{
////				next.setPerspectiveDateStart(newPerspectiveEndDate);
////			}
//		}		
//	}
}