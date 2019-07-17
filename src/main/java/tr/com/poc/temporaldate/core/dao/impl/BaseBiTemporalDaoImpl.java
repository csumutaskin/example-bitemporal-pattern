package tr.com.poc.temporaldate.core.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.dao.annotation.PidDetail;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.model.temporal.BaseTemporalEntity;

/**
 * Base CRUD operations on any Bitemporal Entity
 * @author umutaskin
 *
 * @param <E> any entity that extends {@link BaseBitemporalEntity}
 */
@Component
@SuppressWarnings(value = { "rawtypes", "unchecked"})
@Log4j2
//TODO: Add Oracle 12c identity support.. 
public class BaseBiTemporalDaoImpl<E extends BaseBitemporalEntity>
{
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private BaseBiTemporalDaoHelperImpl<E> baseHelper;
	
	/**
	 * Returns next sequence value on a well known pid sequence column of an entity  
	 * @return next value of the sequence, casted to the related pid type of that entity
	 */
	public Object getSequenceNextValueOfPidColumn()
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		PidDetail pidInfoOfCurrentEntity = baseHelper.getPidInfoOfCurrentEntity(beType, OperationType.UNDEFINED);
		BigInteger sequenceNextValue = (BigInteger)baseHelper.getSequenceNextValue(entityManager, pidInfoOfCurrentEntity.getSequenceName());
		return pidInfoOfCurrentEntity.castGivenValueToPidType(beType, sequenceNextValue);		
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
	 * Returns the pid details of an entity if exists
	 * @param type current database operation waiting to be completed
	 * @return {@link PidDetail}
	 */
	public PidDetail getPidInfoOfCurrentEntity(OperationType type)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		return baseHelper.getPidInfoOfCurrentEntity(beType, type);
	}
	
	/**
	 * Retrieves currently effective entity, from current perspective using primary key id
	 * @param pk primary key to be searched (not the @Pid)
	 * @return {@link BaseTemporalEntity} 
	 */
	public E getEntityWithPrimaryId(final Serializable pk)
	{		
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		return baseHelper.getEntityWithPrimaryIdForUpdateWithLockMode(beType, entityManager, pk, null);
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
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		return baseHelper.getEntityWithNaturalIdForUpdateWithLockMode(beType, entityManager, pid, null, perspectiveDate, effectiveDate);
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
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		return baseHelper.getEntityWithNaturalIdForUpdateWithLockMode(beType, entityManager, pid, LockModeType.PESSIMISTIC_WRITE, perspectiveDate, effectiveDate);
	}
		
	/**
	 * Saves or updates a single entity using the given parameters
	 * @param pid @Pid natural id to be updated (if null data will be persisted)
	 * @param baseEntity entity to be saved or updated
	 * @return {@link BaseTemporalEntity} that is saved or updated
	 */
	public E saveOrUpdateEntityWithNaturalId(Serializable pid, E baseBiTemporalEntity)
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		if(pid == null)//persist
		{
			log.info("Saving entity:{}, with pid: {}", beType.getSimpleName(), pid);
			toReturn = saveEntityWithNaturalIdWithinEffectiveAndPerspectiveDates(beType, baseBiTemporalEntity);
		}
		else//update
		{	
			log.info("Updating entity:{}, with pid: {}", beType.getSimpleName(), pid);
			toReturn = updateEntityWithNaturalIdWithinEffectiveAndPerspectiveDates(beType, pid, baseBiTemporalEntity);
		}
		return toReturn;
	}	
	
	/**
	 * Saves or updates a single DTO using the given parameters
	 * @param pid @Pid natural id to be updated (if null data will be persisted)
	 * @param basebiTemporalDTO DTO to be saved or updated
	 * @param converterClazz class that is used to convert DTO to its related entity
	 * @return {@link BaseTemporalEntity} that is saved or updated
	 */
	public <D extends BaseBitemporalDTO> E saveOrUpdateDTOWithNaturalId(Serializable pid, D basebiTemporalDTO, Class<? extends BaseConverter<E, D>> converterClazz)
	{
		E entityToBeSavedOrUpdated = baseHelper.getRelevantConverter(converterClazz).convertToEntity(basebiTemporalDTO);
		return saveOrUpdateEntityWithNaturalId(pid, entityToBeSavedOrUpdated);	
	}
	
	/**
	 * Persists an entity to its related table
	 * @param beType type of object to be persisted
	 * @param toSave object that is to be persisted
	 * @return {@link E extends BaseEntity}
	 */
	public E saveEntityWithNaturalIdWithinEffectiveAndPerspectiveDates(Class<?> beType, E toSave)
	{
		baseHelper.validateDates(beType, toSave, OperationType.SAVE);
		PidDetail pidDetail = baseHelper.getPidInfoOfCurrentEntity(beType, OperationType.SAVE);
		toSave.setIsDeleted(Boolean.FALSE);
		if(pidDetail.readPidColumnValue(beType, toSave) == null)//For a persist operation @Pid value should be null and read from its own sequence
		{	
			pidDetail.setPidColumnValue(beType, toSave, baseHelper.getSequenceNextValue(entityManager, pidDetail.getSequenceName()));
			entityManager.persist(toSave);
			return toSave;
		}
		throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_ENTITY_PID_COLUMN_SHOULD_BE_NULL, beType.getSimpleName(), toSave.toString());		
	}
	
	/**
	 * Updates an entity within given Effective and Perspective Dates
	 * @param beType type of object to be persisted
	 * @param pid pid value of the object to be updated
	 * @param toUpdate new parameters of the object that will be overridden to the table
	 * @return {@link E extends BaseEntity}
	 */
	public  E updateEntityWithNaturalIdWithinEffectiveAndPerspectiveDates(Class<?> beType, Serializable pid, E toUpdate)
	{			
		baseHelper.validateDates(beType, toUpdate, OperationType.UPDATE);
		PidDetail pidDetail = baseHelper.getPidInfoOfCurrentEntity(beType, OperationType.UPDATE);
		toUpdate.setIsDeleted(Boolean.FALSE);
		pidDetail.setPidColumnValue(beType, toUpdate, pid);
		Collection<E> allEntitiesWithinDates = baseHelper.getAllEntitiesWithinDates(beType, entityManager, pid, toUpdate.getEffectiveDateStart(), toUpdate.getEffectiveDateEnd(), pidDetail);
		baseHelper.updateOldTuplesWithNewDates(entityManager, beType, (List<E>)allEntitiesWithinDates, toUpdate.getPerspectiveDateStart(), toUpdate.getEffectiveDateStart(), toUpdate.getEffectiveDateEnd());
		entityManager.persist(toUpdate);
	/*	baseHelper checkNoPerspectiveDateGapsAfterUpdate baseHelper checkNoEffectiveDateGapsAfterUpdate*/
		return toUpdate;
	}	
	

	/**
	 * Removes - Soft Deletes - all entities with the given criteria
	 * @param beType type of the object to be persisted
	 * @param pid pid value of the pid column of the entity - leave null to select all pid's
	 * @param perspectiveDate perspective time - leave null to select all tuple modifications
	 * @param effectiveDate - effective time - leave null to select all tuples at different effective times
	 */
	public void removeEntityWithNaturalIdWithinEffectiveAndPerspectiveDates(Serializable pid, LocalDateTime perspectiveDate, LocalDateTime effectiveDate)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		List<E> entitiesRetrievedWithLock = baseHelper.getEntityWithNaturalIdForUpdateWithLockMode(beType, entityManager, pid, LockModeType.PESSIMISTIC_WRITE, perspectiveDate, effectiveDate);
		if(CollectionUtils.isEmpty(entitiesRetrievedWithLock))
		{
			return;
		}
		for(E current: entitiesRetrievedWithLock)
		{
			entityManager.remove(current);
		}
	}
	
	/**
	 * Persists entity *DOES NOT USE pid utility
	 * @param toSave object to be persisted
	 * @return {@link BaseBitemporalEntity} object that is persisted
	 */
	public E saveEntityWithPrimaryId(E toSave)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];	
		if(toSave == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_CANNOT_BE_NULL, OperationType.SAVE.toString(), beType.getSimpleName());
		}
		toSave.setIsDeleted(Boolean.FALSE);
		entityManager.persist(toSave);
		return toSave;
	}
}