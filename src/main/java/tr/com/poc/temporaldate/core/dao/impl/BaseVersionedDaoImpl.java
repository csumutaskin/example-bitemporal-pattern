package tr.com.poc.temporaldate.core.dao.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.dao.BaseDao;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.BaseDTO;
import tr.com.poc.temporaldate.core.model.BaseTemporalEntity;

/**
 * CRUD operations on a versioned entity
 * 
 * @author umut
 *
 * @param <E>
 */
@Component
@SuppressWarnings(value = { "rawtypes", "unchecked"})
@Log4j2
public class BaseVersionedDaoImpl<E extends BaseTemporalEntity> implements BaseDao<E> 
{
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
	
	public <D extends BaseDTO> D getDTO(final Serializable pk, Class<? extends BaseConverter<E,D>> baseConverter) 
	{
		return getRelevantConverter(baseConverter).convertToDTO(getEntity(pk));
	}
	
	public List<E> getEntityList()
	{
		return getEntityListWithLockMode(null);
	}

	public List<E> getEntityListForUpdate()
	{
		return getEntityListWithLockMode(LockModeType.PESSIMISTIC_WRITE);		
	}
	
	public <D extends BaseDTO> List<D> getDTOList(Class<? extends BaseConverter<E,D>> converterClass)
	{
		List<E> allEntities = getEntityList();
		return (List<D>) getRelevantConverter(converterClass).convertEntityCollectionToDTOCollection(allEntities);
	}
	
	public <D extends BaseDTO> E saveDTOReturnEntity(D baseDTO, Class converterClass)
	{		
		E persistedEntity = (E) getRelevantConverter(converterClass).convertToEntity(baseDTO);
		//EnrichEntity
		entityManager.persist(persistedEntity);
		entityManager.flush();
		return persistedEntity;
	}
		
	public <D extends BaseDTO> E updateEntityByDTO(Serializable id, D updateDTO, Class<? extends BaseConverter<E,D>> baseConverter)
	{
		if(id == null)
		{
			return null;
		}
		E entityFromDB = getEntityForUpdate(id);
		if(entityFromDB == null)
		{
			return null;
		}
		E convertedEntity = getRelevantConverter(baseConverter).convertToEntity(updateDTO);
		convertedEntity.setCreateDate(entityFromDB.getCreateDate());
		convertedEntity.setCreateUser(entityFromDB.getCreateUser());
		try
		{
			entityManager.merge(setIdusingReflection(convertedEntity, id));
		}
		catch(Exception e)
		{
			String exceptionMessage = "Can not update an entity using DTO. Detail is." + ExceptionUtils.getStackTrace(e);
			log.info("Can not update an entity using DTO. See error log for details.");
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.UPDATE_ENTITY_BY_DTO, exceptionMessage, e);
		}
		return convertedEntity;
	}
	
	public boolean deleteEntity(Serializable id)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		int deleteCount = entityManager.createQuery("delete from "+ beType.getSimpleName() +" e where e.id=:id").setParameter("id", id).executeUpdate();
		entityManager.flush();
		if (deleteCount == 0) 
		{
			log.info("No tuple found to be deleted with id: {}",id);
			return false;
		}			
		return true;
	}
	
	private List<E> getEntityListWithLockMode(LockModeType lockModeType)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		String query = "SELECT BE FROM " + beType.getSimpleName() + " BE";
		Query queryJpa = entityManager.createQuery(query);
		if(lockModeType != null)
		{
			queryJpa.setLockMode(lockModeType);
		}
		return queryJpa.getResultList();
	}
	
	private <D extends BaseDTO> BaseConverter<E,D> getRelevantConverter(Class<? extends BaseConverter<E,D>> baseConverter)
	{
		if(baseConverter == null)
		{
			String errorMessage = "Can not instantiate a user given null base converter object in repository layer. Detail: A null object is given as parameter to the method";	
			log.info("Can not instantiate a user given null base converter object in repository layer. See error log for details.");
			log.error(errorMessage);
			throw new ApplicationException(ExceptionConstants.GET_RELEVANT_CONVERTER_EXCEPTION, errorMessage, new NullPointerException());
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
			throw new ApplicationException(ExceptionConstants.GET_RELEVANT_CONVERTER_EXCEPTION, ExceptionUtils.getMessage(e), e);
		}		
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
	
	private E setIdusingReflection(E baseEntity, Serializable id) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException
	{
	
		Method method = null;		
		try
		{
			Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			method = beType.getMethod(Constants.ID_SETTER_KEY, BigDecimal.class);
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
}
