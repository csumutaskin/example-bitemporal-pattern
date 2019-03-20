package tr.com.poc.temporaldate.core.dao.impl;

import static tr.com.poc.temporaldate.util.Constants.END_OF_EPYS;
import static tr.com.poc.temporaldate.util.Constants.ID_SETTER_KEY;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.model.BaseDTO;
import tr.com.poc.temporaldate.util.ExceptionConstants;;

@Component
@SuppressWarnings(value = { "rawtypes", "unchecked"})
@Log4j2
public class BaseBiTemporalDao<E extends BaseBitemporalEntity>
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
	
	public E getEntity(final Serializable pk)//currently effective from current perspective
	{		
		return getEntityForUpdateWithLockMode(pk, null);				
	}
	
	public E getEntityForUpdate(final Serializable pk)//currently effective from current perspective
	{
		return getEntityForUpdateWithLockMode(pk, LockModeType.PESSIMISTIC_WRITE);
	}
	
	public E getEntityAtEffectiveTime(final Serializable pk, Date effectiveDate)//effective at a certain date from current perspective
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query selectWithinEffectiveTime = entityManager.createQuery("SELECT * FROM " + beType.getSimpleName() + " E WHERE E.effectiveDateStart <= :effectiveDate and E.effectiveDateEnd >= :effectiveDate and sysdate >= recordDateStart and sysdate <= recordDateEnd and E.id = :id");		
		selectWithinEffectiveTime.setParameter("id", pk);
		selectWithinEffectiveTime.setParameter("effectiveDate", effectiveDate);
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
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_DATE, ExceptionUtils.getMessage(nure) ,nure);	
		}
		catch(Exception e)
		{
			String exceptionMessage = UNEXCPECTED_QUERY_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_DATE, ExceptionUtils.getMessage(e) ,e);	
		}
		return toReturn;
	}
	
	public E getEntityAtEffectiveTimeFromPerspectiveTime(final Serializable pk, Date effectiveDate, Date perspectiveDate)//effective at a certain date from a certain perspective (not now)
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query selectWithinEffectiveTimeFromPerspectiveDate = entityManager.createQuery("SELECT * FROM " + beType.getSimpleName() + " E WHERE E.effectiveDateStart <= :effectiveDate and E.effectiveDateEnd >= :effectiveDate and :perspectiveDate >= recordDateStart and :perspectiveDate <= recordDateEnd and id = :id");
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("effectiveDate", effectiveDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("perspectiveDate", perspectiveDate);
		selectWithinEffectiveTimeFromPerspectiveDate.setParameter("id", pk);
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
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_TIME_FROM_PERSPECTIVE_TIME, ExceptionUtils.getMessage(nure) ,nure);	
		}
		catch(Exception e)
		{
			String exceptionMessage = UNEXCPECTED_QUERY_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_TIME_FROM_PERSPECTIVE_TIME, ExceptionUtils.getMessage(e) ,e);	
		}
		return toReturn;
	}
	
	public E saveAndFlushEntity(E baseEntity, Date effectiveStartDate, Date effectiveEndDate)
	{		
		return saveEntityWithFlushOption(baseEntity, true, effectiveStartDate, effectiveEndDate);
	}
	
    public E saveEntity(E baseEntity, Date effectiveStartDate, Date effectiveEndDate)
    {
    	return saveEntityWithFlushOption(baseEntity, false, effectiveStartDate, effectiveEndDate);
    }
	
	private E saveEntityWithFlushOption(E baseEntity, boolean flushNeeded, Date effectiveStartDate, Date effectiveEndDate)
	{			
		entityManager.persist(enrichPerspectiveDate(baseEntity,effectiveStartDate, effectiveEndDate));
		if(flushNeeded)
		{
			entityManager.flush();
		}
		return baseEntity;
	}
	
	public <D extends BaseDTO> E updateEntity(Serializable id, E newEntity)
	{
		if(id == null)
		{
			return null;
		}
		E toReturn = null;
		E entityFromDB = getEntityForUpdate(id);
		if(entityFromDB == null)
		{
			return toReturn;
		}
		Date now = new Date();//TODO: yeni kaydin create date i aldir.
		try
		{
			entityFromDB.setEffectiveDateEnd(now);
			toReturn = saveEntity(newEntity, now, END_OF_EPYS);
		}
		catch(Exception e)
		{
			String exceptionMessage = "Can not update (by adding a new item with new perspective dates) an entity using DTO. Detail is." + ExceptionUtils.getStackTrace(e);
			log.info("Can not update (by adding a new item with new perspective dates) an entity using DTO. See error log for details.");
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_UPDATE_ENTITY, exceptionMessage, e);
		}
		return toReturn;
	}
	
	private E getEntityForUpdateWithLockMode(final Serializable pk, LockModeType lockModeType)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		Query selectCurrentDataUsingEffectiveTimeParameter = entityManager.createQuery("SELECT * FROM " + beType.getSimpleName() + " E WHERE E.effectiveDateStart <= sysdate and E.effectiveDateEnd >= sysdate and sysdate >= recordDateStart and sysdate <= recordDateEnd and E.id = :id");		
		selectCurrentDataUsingEffectiveTimeParameter.setParameter("id", pk);
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
	
	private E enrichPerspectiveDate(E baseEntity, Date effectiveStartDate, Date effectiveEndDate)
	{
		baseEntity.setRecordDateStart(new Date());//TODO: Audit create date alinmali...
		baseEntity.setRecordDateEnd(END_OF_EPYS);
		baseEntity.setEffectiveDateStart(effectiveStartDate);
		baseEntity.setEffectiveDateEnd(effectiveEndDate);
		return baseEntity;
	}
}