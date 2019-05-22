package tr.com.poc.temporaldate.core.dao.impl;

import static tr.com.poc.temporaldate.common.Constants.ID_SETTER_KEY;
import static tr.com.poc.temporaldate.core.util.DateUtils.END_OF_SOFTWARE;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.model.temporal.BaseTemporalEntity;
import tr.com.poc.temporaldate.core.util.DateUtils;
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
public class BaseBiTemporalDaoImpl<E extends BaseBitemporalEntity>
{
	private static final String NO_SINGLE_RESULT_EXC_STRING = "Returning null since NoResultException is thrown and caught";
	private static final String NON_UNIQUE_PERSPECTIVE_EXC_STRING = "Non unique single item returned from a perspective get query"; 
	private static final String NON_UNIQUE_PERSPECTIVE_EXC_PREFIX_STRING = "Non unique single item returned from a perspective get query. Detail: "; 
	private static final String UNEXCPECTED_QUERY_EXC_PREFIX_STRING = "Exception running a query in repository. Detail: ";
	
	@PersistenceContext
	private EntityManager entityManager;

	public EntityManager getEntityManager()
	{
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
	
	/**
	 * Retrieves currently effective entity from current perspective using natural id
	 * @param pk primary key to be searched
	 * @return {@link BaseTemporalEntity} object
	 */
	public E getEntity(final Serializable pk)
	{		
		return getEntityForUpdateWithLockMode(pk, null);				
	}
	
	/**
	 * Retrieves currently effective entity from current perspective with pessimistic lock for updating
	 * @param pk primary key to be searched
	 * @return {@link BaseTemporalEntity} object
	 */
	/*
	public E getEntityForUpdate(final Serializable pk)
	{
		return getEntityForUpdateWithLockMode(pk, LockModeType.PESSIMISTIC_WRITE);
	}*/
	
	/**
	 * Retrieves entity effective at a given time from current perspective
	 * @param pk primary key to be searched
	 * @param effectiveDate
	 * @return {@link BaseTemporalEntity} object
	 */
	public E getEntityAtEffectiveTime(final Serializable pk, Date effectiveDate)
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query selectWithinEffectiveTime = entityManager.createQuery("SELECT BEntity FROM " + beType.getSimpleName() + " BEntity WHERE BEntity.effectiveDateStart <= :effectiveDateQueried and BEntity.effectiveDateEnd >= :effectiveDateQueried and sysdate >= recordDateStart and sysdate <= recordDateEnd and BEntity.id = :id");		
		selectWithinEffectiveTime.setParameter("id", pk);
		selectWithinEffectiveTime.setParameter("effectiveDateQueried", effectiveDate);
		try
		{
			toReturn = (E) selectWithinEffectiveTime.getSingleResult();			
		}
		catch(NoResultException nre)
		{
			log.info(NO_SINGLE_RESULT_EXC_STRING);	
			toReturn = null;
		}
		catch(NonUniqueResultException nure)
		{
			String exceptionMessage = NON_UNIQUE_PERSPECTIVE_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(nure);	
			log.info(NON_UNIQUE_PERSPECTIVE_EXC_STRING);
			log.error(exceptionMessage);				
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_DATE, nure);	
		}
		catch(Exception e)
		{
			String exceptionMessage = UNEXCPECTED_QUERY_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_DATE, e);	
		}
		return toReturn;
	}
	
	/**
	 * Retrieves entity effective at a given time from a given perspective
	 * @param pk primary key to be searched
	 * @param effectiveDate actual date for the tuple
	 * @param perspectiveDate from which time line we are looking at the tuples
	 * @return {@link BaseTemporalEntity} object
	 */
	public E getEntityAtEffectiveTimeFromPerspectiveTime(final Serializable pk, Date effectiveDate, Date perspectiveDate)//effective at a certain date from a certain perspective (not now)
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query selectWithinEffectiveTimeFromPerspectiveDate = entityManager.createQuery("SELECT BE FROM " + beType.getSimpleName() + " BE WHERE BE.effectiveDateStart <= :effectiveDate and BE.effectiveDateEnd >= :effectiveDate and :perspectiveDate >= recordDateStart and :perspectiveDate <= recordDateEnd and BE.id = :id");
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveDate", effectiveDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("perspectiveDate", perspectiveDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter(Constants.ID_COLUMN_KEY, pk);
		try
		{
			toReturn = (E) selectWithinEffectiveTimeFromPerspectiveDate.getSingleResult();			
		}
		catch(NoResultException nre)
		{
			log.info(NO_SINGLE_RESULT_EXC_STRING);	
			toReturn = null;
		}
		catch(NonUniqueResultException nure)
		{
			String exceptionMessage = NON_UNIQUE_PERSPECTIVE_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(nure);	
			log.info(NON_UNIQUE_PERSPECTIVE_EXC_STRING);
			log.error(exceptionMessage);				
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_TIME_FROM_PERSPECTIVE_TIME, nure);	
		}
		catch(Exception e)
		{
			String exceptionMessage = UNEXCPECTED_QUERY_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_TIME_FROM_PERSPECTIVE_TIME, e);	
		}
		return toReturn;
	}
	
	/**
	 * Saves or updates (with flush) entity from current perspective
	 * @param id primary key to be updated (if null data will be persisted)
	 * @param baseEntity entity to be saved or updated
	 * @param effectiveStartDate actual start date where the tuple is active
	 * @param effectiveEndDate actual final date where the tuple is active
	 * @param mergeAndRearrangeDates if true no tuple having the same id is deleted, instead the given parameters withing given date range is merged with the older same id tuple in the timeline
	 * @return {@link BaseTemporalEntity} that is saved or updated
	 */
	public E saveOrUpdateEntityWithFlush(Serializable id, E baseEntity, Date effectiveStartDate, Date effectiveEndDate)
	{		
		return saveOrUpdateEntityWithFlushOption(id, baseEntity, true, effectiveStartDate, effectiveEndDate);
	}
	
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
	
	/**
	 * Saves or updates entity from current perspective
	 * @param id primary key to be updated (if null data will be persisted)
	 * @param baseEntity entity to be saved or updated
	 * @param effectiveStartDate actual start date where the tuple is active
	 * @param effectiveEndDate actual final date where the tuple is active
 	 * @param mergeAndRearrangeDates if true no tuple having the same id is deleted, instead the given parameters withing given date range is merged with the older same id tuple in the timeline
	 * @return {@link BaseTemporalEntity} that is saved or updated
	 */
    public E saveorUpdateEntity(Serializable id, E baseEntity, Date effectiveStartDate, Date effectiveEndDate)
    {
    	return saveOrUpdateEntityWithFlushOption(id, baseEntity, false, effectiveStartDate, effectiveEndDate);
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
    	//TODO: Fix on Monday... always get overlapping entities with the same id if exist and rearrange older tuples dates using the newer tuple date!!!! 
    	E baseEntity = getRelevantConverter(baseConverter).convertToEntity(updateDTO);
    	return saveOrUpdateEntityWithFlushOption(id, baseEntity, false, baseEntity.getEffectiveDateStart(), baseEntity.getEffectiveDateEnd());
    }
    
	/**
	 * Retrieves all entities that intersect the given effective start and end dates
	 * @param pk primary key to be searched
	 * @return {@link BaseTemporalEntity} object
	 */
	public Collection<E> getAllEntitiesThatIntersectBeginAndEndDate(final Serializable pk, Date effectiveStartDate, Date effectiveEndDate)
	{
		Collection<E> toReturnCollection = new ArrayList<>();
		if(pk == null)
		{
			return toReturnCollection;
		}
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query selectWithinEffectiveTimeFromPerspectiveDate = entityManager.createQuery("SELECT E FROM " + beType.getSimpleName() + " E WHERE E.effectiveDateStart < :effectiveEndDate and E.effectiveDateEnd > :effectiveStartDate and sysdate >= recordDateStart and sysdate <= recordDateEnd and id = :id");
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveStartDate", effectiveStartDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveEndDate", effectiveEndDate);	
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter(Constants.ID_COLUMN_KEY, pk);
		try
		{
			toReturnCollection = (List<E>) selectWithinEffectiveTimeFromPerspectiveDate.getResultList();		
		}
		catch(Exception e)
		{
			String errorMessage = "Can not retrieve all tuples of type " + beType.getSimpleName() +"  withing given effective date range with a begin date of: " + effectiveStartDate + " and an end date of: " + effectiveEndDate + ". Detail: " + ExceptionUtils.getStackTrace(e);
			log.error(errorMessage);
			log.info("Can not retrieve all tuples of type " + beType.getSimpleName() + "  within given effective date. See errog log for details.");					 
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ALL_ENTITIES_THAT_INTERSECT_BEGIN_AND_END_DATE, e);	
		}		
		return toReturnCollection;
	}
	
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
	
	public <D extends BaseBitemporalDTO> D getDTOAtEffectiveDate(final Serializable pk, Class<? extends BaseConverter<E,D>> baseConverter, Date effectiveDate) 
	{
		return getRelevantConverter(baseConverter).convertToDTO(getEntityAtEffectiveTime(pk, effectiveDate));
	}
	
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
		queryJpa.setParameter("effectiveDate", effectiveDate);
		if(lockModeType != null)
		{
			queryJpa.setLockMode(lockModeType);
		}
		return queryJpa.getResultList();
	}
	
    /* Saves or Updates entity */
	private E saveOrUpdateEntityWithFlushOption(Serializable pk, E baseEntity, boolean flushNeeded, Date effectiveStartDate, Date effectiveEndDate)
	{	
		Collection<E> entitiesIntersected = getAllEntitiesThatIntersectBeginAndEndDate(pk, effectiveStartDate, effectiveEndDate);
		if(CollectionUtils.isEmpty(entitiesIntersected))
		{
			entityManager.persist(enrichDateColumns(baseEntity,effectiveStartDate, effectiveEndDate));
		}
		else //update currently found entity
		{
			Collections.sort((List<E>)entitiesIntersected, new SortBaseEntityByEffectiveStartDateComparator<E>());
			boolean endDateCoveredByAnyExistingTuple = false;
			for(E current:entitiesIntersected)
			{
				//TODO: comparator sort by date begin, then start updating new dates using the given date by end user and also merge the new data 
				//if both start and end dates match, update by merging new data
				//if at least one date match, insert new data and limit the old data times..
				if(DateUtils.dateBetweenDates(effectiveStartDate, current.getEffectiveDateStart(), current.getEffectiveDateEnd(), false))
				{
					current.setEffectiveDateEnd(effectiveStartDate);
					continue;
				}
				if(DateUtils.dateBetweenDates(effectiveEndDate, current.getEffectiveDateStart(), current.getEffectiveDateEnd(), false))
				{
					endDateCoveredByAnyExistingTuple = true;					
					current.setEffectiveDateStart(effectiveEndDate);
				}
			}
			if(!endDateCoveredByAnyExistingTuple)
			{
				entityManager.persist(enrichDateColumns(baseEntity,effectiveStartDate, effectiveEndDate));
			}
		}
		if(flushNeeded)
		{
			entityManager.flush();
		}
		return baseEntity;
	}
	
    /* Saves or Updates entity with pessimistic lock in case of update */
	private E getEntityForUpdateWithLockMode(final Serializable pk, LockModeType lockModeType)
	{
		 if(pk == null)
		 {
			return null;
		 }
		 Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		 Query selectCurrentDataUsingEffectiveTimeParameter = null;
		 selectCurrentDataUsingEffectiveTimeParameter = entityManager.createQuery("SELECT E FROM " + beType.getSimpleName() + " E WHERE E.effectiveDateStart <= sysdate and E.effectiveDateEnd >= sysdate and sysdate >= recordDateStart and sysdate <= recordDateEnd and E.id = :id");
		 selectCurrentDataUsingEffectiveTimeParameter.setParameter(Constants.ID_COLUMN_KEY, pk);
		 if(lockModeType != null)
		 {
			selectCurrentDataUsingEffectiveTimeParameter.setLockMode(lockModeType);
			return (E) selectCurrentDataUsingEffectiveTimeParameter.getSingleResult();
		 }
		 else
		 {
			return (E) selectCurrentDataUsingEffectiveTimeParameter.getSingleResult();
		 }
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
	private E enrichDateColumns(E baseEntity, Date effectiveStartDate, Date effectiveEndDate)
	{
		baseEntity.setRecordDateStart(new Date());//TODO: Audit create date alinmali...
		baseEntity.setRecordDateEnd(END_OF_SOFTWARE);
		baseEntity.setEffectiveDateStart(effectiveStartDate);
		baseEntity.setEffectiveDateEnd(effectiveEndDate);
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
}