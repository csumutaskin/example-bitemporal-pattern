package tr.com.poc.temporaldate.core.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
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
import tr.com.poc.temporaldate.util.ExceptionConstants;

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
	
	public E getEntity(final Serializable pk)
	{		
		return getEntityForUpdateWithLockMode(pk, null);				
	}
	
	public E getEntityForUpdate(final Serializable pk)
	{
		return getEntityForUpdateWithLockMode(pk, LockModeType.PESSIMISTIC_WRITE);
	}
	
	public E getEntityAtEffectiveTime(final Serializable pk, Date effectiveDate)
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query selectWithPerspective = entityManager.createQuery("SELECT * FROM " + beType.getSimpleName() + " E WHERE E.effectiveDateStart <= :effectiveDate and E.effectiveDateEnd >= :effectiveDate and sysdate >= recordDateStart and sysdate <= recordDateStart");		
		try
		{
			toReturn = (E) selectWithPerspective.setParameter("effectiveDate", effectiveDate).getSingleResult();			
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
			throw new ApplicationException(ExceptionConstants.GET_ENTITY_AT_EFFECTIVE_DATE, ExceptionUtils.getMessage(nure) ,nure);	
		}
		catch(Exception e)
		{
			String exceptionMessage = UNEXCPECTED_QUERY_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.GET_ENTITY_AT_EFFECTIVE_DATE, ExceptionUtils.getMessage(e) ,e);	
		}
		return toReturn;
	}
	
	public E getEntityAtEffectiveTimeFromPerspectiveTime(final Serializable pk, Date effectiveDate, Date perspectiveTime)
	{
		E toReturn = null;
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Query selectWithPerspective = entityManager.createQuery("SELECT * FROM " + beType.getSimpleName() + " E WHERE E.effectiveDate ??????= :effectiveDate");//and from perspective time		
		try
		{
			toReturn = (E) selectWithPerspective.setParameter("effectiveDate", effectiveDate).getSingleResult();			
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
			throw new ApplicationException(ExceptionConstants.GET_ENTITY_AT_EFFECTIVE_DATE, ExceptionUtils.getMessage(nure) ,nure);	
		}
		catch(Exception e)
		{
			String exceptionMessage = UNEXCPECTED_QUERY_EXC_PREFIX_STRING + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.GET_ENTITY_AT_EFFECTIVE_DATE, ExceptionUtils.getMessage(e) ,e);	
		}
		return toReturn;
	}
	
	private E getEntityForUpdateWithLockMode(final Serializable pk, LockModeType lockModeType)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		if(lockModeType != null)
		{
			return (E) entityManager.find(beType, pk, lockModeType);
		}
		else
		{
			return (E) entityManager.find(beType, pk);		 
		}		
	}
}