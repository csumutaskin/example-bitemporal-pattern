package tr.com.poc.temporaldate.core.dao.impl;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.BaseDTO;
import tr.com.poc.temporaldate.core.model.BaseEntity;
import tr.com.poc.temporaldate.util.Constants;
import tr.com.poc.temporaldate.util.ExceptionConstants;

/**
 * Base Data Access Operations. Includes generic operations for saving, updating, deleting and retrieving tuples according to different criteria. Do not use/inject this component directly to the service layer. 
 * Instead, use this utility as a super class to each entity's data access operation classes.
 * 
 * E.g public class MyDao extends BaseEntity<MyObject> {} And inject MyDao to MyService class and so on...
 * 
 * @author umutaskin
 * @param <baseEntity
 *            extends BaseEntity> Needs a generic type of any BaseEntity extending object to auto detect the database table/entity mapping
 */

@Component
@SuppressWarnings(value = { "rawtypes", "unchecked"})
@Log4j2
public class BaseDaoImpl<E extends BaseEntity>
{
	//TODO: Enrich entity...
	
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

	public List<E> getEntityList()
	{
		return getEntityListWithLockMode(null);
	}

	public List<E> getEntityListForUpdate()
	{
		return getEntityListWithLockMode(LockModeType.PESSIMISTIC_WRITE);		
	}

	public E getEntity(final Serializable pk)
	{		
		return getEntityForUpdateWithLockMode(pk, null);				
	}
	
	public E getEntityForUpdate(final Serializable pk)
	{
		return getEntityForUpdateWithLockMode(pk, LockModeType.PESSIMISTIC_WRITE);
	}

	public E updateEntity(E toUpdate)
	{				
		E merge = entityManager.merge(toUpdate);
		entityManager.flush();
		return merge;
	}

	public boolean deleteEntityListByEntityCriteria(E toDeleteFilter)
	{
		List<E> entityListByEntityCriteria = getEntityListByEntityCriteria(toDeleteFilter);
		deleteEntityList(entityListByEntityCriteria);
		return true;
	}

	public E saveAndFlushEntity(E baseEntity, boolean flushNeeded)
	{		
		return saveEntityWithFlushOption(baseEntity, true);
	}
	
    public E saveEntity(E baseEntity)
    {
    	return saveEntityWithFlushOption(baseEntity, false);
    }

	public void deleteEntity(E baseEntity)
	{
		entityManager.remove(baseEntity);
		entityManager.flush();		
	}

	public void deleteEntityList(List<E> entityList)
	{
		if(CollectionUtils.isEmpty(entityList))
		{
			return;
		}
		for (E item : entityList)
		{
			entityManager.remove(item);
			entityManager.flush();
		}
	}

	public List<E> getEntityListByEntityCriteria(E searchCriteria)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteria = builder.createQuery(beType);
		Root<E> pRoot = criteria.from(beType);
		Predicate base = builder.conjunction();		
		for (Field field : beType.getDeclaredFields())
		{
			field.setAccessible(true);
			try
			{
				if (field.get(searchCriteria) != null)
				{
					base = builder.and(builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria)), base);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				String exceptionMessage = "Exception Building a criteria in repository. Detail: " + ExceptionUtils.getStackTrace(e);
				log.info("An unexpected exception. See error log for details");
				log.error(exceptionMessage);
				throw new ApplicationException(ExceptionConstants.GET_ENTITY_LIST_BY_ENTITY_CRITERIA_EXCEPTION, ExceptionUtils.getMessage(e) ,e);				
			}
		}
		criteria.select(pRoot);
		criteria.where(base);
		return entityManager.createQuery(criteria).getResultList();
	}

	public Long getRowCountByEntityCriteria(E searchCriteria)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
		Root<E> pRoot = criteria.from(beType);
		Predicate base = builder.conjunction();		
		for (Field field : beType.getDeclaredFields())
		{
			field.setAccessible(true);
			try
			{
				if (field.get(searchCriteria) != null)
				{
					base = builder.and(builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria)), base);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				String exceptionMessage = "Exception Building a criteria in repository. Detail: " + ExceptionUtils.getStackTrace(e);
				log.info("An unexpected exception. See error log for details");
				log.error(exceptionMessage);
				throw new ApplicationException(ExceptionConstants.GET_ROW_COUNT_BY_ENTITY_CRITERIA_EXCEPTION, ExceptionUtils.getMessage(e) ,e);			
			}
		}
		criteria.select(builder.count(pRoot));
		criteria.where(base);
		return entityManager.createQuery(criteria).getSingleResult();
	}

	public List<E> getEntityListByEntityCriteriaWithPagination(E searchCriteria, int first, int pageSize)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteria = builder.createQuery(beType);
		Root<E> pRoot = criteria.from(beType);
		Predicate base = builder.conjunction();		
		for (Field field : beType.getDeclaredFields())
		{
			field.setAccessible(true);
			try
			{
				if (field.get(searchCriteria) != null)
				{
					base = builder.and(builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria)), base);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				String exceptionMessage = "Exception Building a criteria in repository. Detail: " + ExceptionUtils.getStackTrace(e);
				log.info("An unexpected exception. See error log for details");
				log.error(exceptionMessage);
				throw new ApplicationException(ExceptionConstants.GET_ENTITY_LIST_COUNT_BY_ENTITY_CRITERIA_WITH_PAGINATION, ExceptionUtils.getMessage(e) ,e);	
			}
		}
		criteria.select(pRoot);
		criteria.where(base);
		criteria.orderBy(builder.asc(pRoot.get(Constants.ID_COLUMN_KEY)));
		TypedQuery<E> typedQuery = entityManager.createQuery(criteria);
		typedQuery.setFirstResult(first);
		typedQuery.setMaxResults(pageSize);
		return typedQuery.getResultList();
	}

	public E getEntityByEntityCriteria(E searchCriteria)
	{
		E toReturn;
		Class beType;
		try
		{
			beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();
			CriteriaQuery<E> criteria = builder.createQuery(beType);
			Root<E> pRoot = criteria.from(beType);
			Predicate base = builder.conjunction();
			for (Field field : beType.getDeclaredFields())
			{
				field.setAccessible(true);
				if (field.get(searchCriteria) != null)
				{
					base = builder.and(builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria)), base);
				}
			}
			criteria.select(pRoot);
			criteria.where(base);
			toReturn = entityManager.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException nre)
		{
			log.info("Returning null since NoResultException is thrown and caught");			
			return null;
		}
		catch (Exception e)
		{
			String exceptionMessage = "Exception Building a criteria in repository. Detail: " + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.GET_ENTITY_BY_ENTITY_CRITERIA, ExceptionUtils.getMessage(e) ,e);	
		}
		return toReturn;
	}

	public E getEntityForUpdateByEntityCriteria(E searchCriteria)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteria = builder.createQuery(beType);
		Root<E> pRoot = criteria.from(beType);
		Predicate base = builder.conjunction();		
		for (Field field : beType.getDeclaredFields())
		{
			field.setAccessible(true);
			try
			{
				if (field.get(searchCriteria) != null)
				{
					base = builder.and(builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria)), base);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				String exceptionMessage = "Exception Building a criteria in repository. Detail: " + ExceptionUtils.getStackTrace(e);	
				log.error(exceptionMessage);
				throw new ApplicationException(ExceptionConstants.GET_ENTITY_FOR_UPDATE_BY_ENTITY_CRITERIA, ExceptionUtils.getMessage(e) ,e);				
			}
		}
		criteria.select(pRoot);
		criteria.where(base);
		TypedQuery<E> createQuery = entityManager.createQuery(criteria);
		createQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		try
		{
			return entityManager.createQuery(criteria).getSingleResult();
		}
		catch (NoResultException nre)
		{
			log.info("Returning null since NoResultException is thrown and caught");	
			return null;
		}		
	}

	public List<E> getEntityListByEntityORCriteria(E searchCriteria)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteria = builder.createQuery(beType);
		Root<E> pRoot = criteria.from(beType);
		Predicate base = builder.conjunction();		
		boolean firstEntry = true;
		for (Field field : beType.getDeclaredFields())
		{
			field.setAccessible(true);
			try
			{
				if (firstEntry)// to prevent 1=1' at the end of the query
				{
					base = builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria));
					firstEntry = false;
					continue;
				}
				if (field.get(searchCriteria) != null && !"id".equalsIgnoreCase(field.getName()))
				{
					base = builder.or(builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria)), base);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				String exceptionMessage = "Exception Building a criteria in repository. Detail: " + ExceptionUtils.getStackTrace(e);	
				log.error(exceptionMessage);
				throw new ApplicationException(ExceptionConstants.GET_ENTITY_LIST_BY_ENTITY_OR_CRITERIA, ExceptionUtils.getMessage(e) ,e);
			}
		}
		criteria.select(pRoot);
		criteria.where(base);
		return entityManager.createQuery(criteria).getResultList();
	}

	public List<E> getEntityListForUpdateByEntityCriteria(E searchCriteria)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<E> criteria = builder.createQuery(beType);
		Root<E> pRoot = criteria.from(beType);
		Predicate base = builder.conjunction();		
		for (Field field : beType.getDeclaredFields())
		{
			field.setAccessible(true);
			try
			{
				if (field.get(searchCriteria) != null && !"id".equalsIgnoreCase(field.getName()))
				{
					base = builder.and(builder.equal(pRoot.<Comparable>get(field.getName()), field.get(searchCriteria)), base);
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				String exceptionMessage = "Exception Building a criteria in repository. Detail: " + ExceptionUtils.getStackTrace(e);	
				log.error(exceptionMessage);
				throw new ApplicationException(ExceptionConstants.GET_ENTITY_LIST_FOR_UPDATE_BY_ENTITY_CRITERIA, ExceptionUtils.getMessage(e) ,e);
			}
		}
		criteria.select(pRoot);
		criteria.where(base);
		Query query = entityManager.createQuery(criteria);
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		return query.getResultList();
	}

	public Long getCount()
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		return (Long) getEntityManager().createQuery("SELECT COUNT(*) FROM " + beType.getSimpleName()).getSingleResult();
	}

	public List<E> getEntityListByNamedQuery(String namedQueryName, Object... parameters)
	{				
		Query query = entityManager.createNamedQuery(namedQueryName);
		int parameterIndex = 1;
		for (Object parameter : parameters)
		{
			query.setParameter(parameterIndex++, parameter);
		}
		return query.getResultList();
	}

	public List<E> getEntityListForUpdateByNamedQuery(String namedQueryName, Object... parameters)
	{
		Query query = entityManager.createNamedQuery(namedQueryName);
		int parameterIndex = 1;
		for (Object parameter : parameters)
		{
			query.setParameter(parameterIndex++, parameter);
		}
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		return query.getResultList();
	}

	public int runNamedQuery(String namedQueryName, Object... parameters)
	{
		Query query = entityManager.createNamedQuery(namedQueryName);
		int parameterIndex = 1;
		for (Object parameter : parameters)
		{
			query.setParameter(parameterIndex++, parameter);
		}
		return query.executeUpdate();
	}

	public E getEntityByNamedQuery(String namedQueryName, Object... parameters)
	{
		E toReturn = null;
		Query query = entityManager.createNamedQuery(namedQueryName);
		int parameterIndex = 1;
		for (Object parameter : parameters)
		{
			query.setParameter(parameterIndex++, parameter);
		}
		try
		{
			toReturn = (E) query.getSingleResult();
		}
		catch (NoResultException nre)
		{
			log.info("Returning null since NoResultException is thrown and caught");	
			return null;
		}
		return toReturn;
	}

	public E getEntityForUpdateByNamedQuery(String namedQueryName, Object... parameters)
	{				
		Query query = entityManager.createNamedQuery(namedQueryName);
		int parameterIndex = 1;
		for (Object parameter : parameters)
		{
			query.setParameter(parameterIndex++, parameter);
		}
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);

		E toReturn;
		try
		{
			toReturn = (E) query.getSingleResult();
		}
		catch (NoResultException nre)
		{
			log.info("Returning null since NoResultException is thrown and caught");	
			return null;
		}
		return toReturn;
	}

	public boolean batchInsert(Collection<E> collection)
	{				
		Iterator<E> iterator = collection.iterator();
		int count = 0;
		while (iterator.hasNext())
		{
			E next = iterator.next();			
			entityManager.persist(next);
			if (count == Constants.REPOSITORY_BULK_TUPLE_SIZE_BEFORE_FLUSH)
			{
				entityManager.flush();
				count = 0;
			}
			count++;
		}
		entityManager.flush();
		return true;
	}

	public boolean batchUpdate(Collection<E> collection)
	{				
		Iterator<E> iterator = collection.iterator();
		int count = 0;
		while (iterator.hasNext())
		{
			E next = iterator.next();			
			entityManager.merge(next);
			if (count == Constants.REPOSITORY_BULK_TUPLE_SIZE_BEFORE_FLUSH)
			{
				entityManager.flush();
				count = 0;
			}
			count++;
		}
		entityManager.flush();
		return true;
	}

	public <D extends BaseDTO> List<D> getDTOList(Class dtoClassToReturn, Class<? extends BaseConverter<E,D>> converterClass)
	{
		List<E> allEntities = getEntityList();
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> List<D> getDTOListForUpdate(Class dtoClassToReturn, Class converterClass)
	{
		List<E> allEntities = getEntityListForUpdate();
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> D updateEntityReturnDTO(Class dtoClassToReturn, E toUpdate, Class converterClass)
	{
		//EnrichEntity
		E updated = entityManager.merge(toUpdate);
		entityManager.flush();		
		return (D) getRelevantConverter(converterClass).convertToDTO(updated);
	}

	public <D extends BaseDTO> D getEntityById(Class dtoClassToReturn, Long pk, Class converterClass)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];		
		E entity = (E) entityManager.find(beType, pk);
		return (D) getRelevantConverter(converterClass).convertToDTO(entity);		
	}

	public <D extends BaseDTO> D saveEntityReturnDTO(Class dtoClassToReturn, E peristedEntity, Class converterClass)
	{
		//EnrichEntity
		entityManager.persist(peristedEntity);
		entityManager.flush();		
		return (D) getRelevantConverter(converterClass).convertToDTO(peristedEntity);
	}

	public <D extends BaseDTO> List<D> getDTOListByEntityCriteria(Class dtoClassToReturn, E searchCriteria, Class converterClass)
	{
		List<E> allEntities = getEntityListByEntityCriteria(searchCriteria);		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> List<D> getDTOListByEntityCriteriaWithPagination(Class dtoClassToReturn, E searchCriteria, Class converterClass, int firstResult, int pageSize)
	{
		List<E> allEntities = getEntityListByEntityCriteriaWithPagination(searchCriteria, firstResult, pageSize);		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> List<D> getDTOListForUpdateByEntityCriteria(Class dtoClassToReturn, E searchCriteria, Class converterClass)
	{
		List<E> allEntities = getEntityListForUpdateByEntityCriteria(searchCriteria);		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> List<D> getDTOListByNamedQuery(Class dtoClassToReturn, String namedQueryName, Class converterClass, Object... parameters)
	{
		List<E> listWithNamedQuery = getEntityListByNamedQuery(namedQueryName, parameters);		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(listWithNamedQuery);
	}

	public <D extends BaseDTO> List<D> getDTOListForUpdateByNamedQuery(Class dtoClassToReturn, String namedQueryName, Class converterClass, Object... parameters)
	{
		List<E> listWithNamedQuery = getEntityListForUpdateByNamedQuery(namedQueryName, parameters);		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(listWithNamedQuery);
	}

	public <D extends BaseDTO> D getDTOByNamedQuery(Class dtoClassToReturn, String namedQueryName, Class converterClass, Object... parameters)
	{
		E singleEntity = getEntityByNamedQuery(namedQueryName, parameters);		
		return (D) getRelevantConverter(converterClass).convertToDTO(singleEntity);
	}

	public <D extends BaseDTO> D getDTOForUpdateByNamedQuery(Class dtoClassToReturn, String namedQueryName, Class converterClass, Object... parameters)
	{
		E singleEntity = getEntityForUpdateByNamedQuery(namedQueryName, parameters);		
		return (D) getRelevantConverter(converterClass).convertToDTO(singleEntity);
	}

	public <D extends BaseDTO> E updateDTOByDtoIDCriteriaReturnEntity(D idSearchCriteria, Class converterClass)
	{
		E entity = getEntityUsingDTOWithId(idSearchCriteria);
		if (entity == null)
		{
			return null;
		}
		//EnrichEntity		
		E bdTObe = (E) getRelevantConverter(converterClass).convertToEntity(idSearchCriteria);
		setNotNullAndNotIdParametersOfSameType(bdTObe, entity, true);		
		return entity;
	}

	public <D extends BaseDTO> E saveDTOReturnEntity(D baseDTO, Class converterClass)
	{		
		E persistedEntity = (E) getRelevantConverter(converterClass).convertToEntity(baseDTO);
		//EnrichEntity
		entityManager.persist(persistedEntity);
		entityManager.flush();
		return persistedEntity;
	}

	public <D extends BaseDTO> E deleteDTOReturnDeletedEntity(D baseDTO)
	{
		E entity = getEntityUsingDTOWithId(baseDTO);
		if (entity != null)
		{
			deleteEntity(entity);
			entityManager.flush();
		}
		return entity;
	}

	public <D extends BaseDTO> List<E> getEntityListByDTOCriteria(D searchCriteria, Class converterClass)
	{		
		E persistedEntity = (E) getRelevantConverter(converterClass).convertToEntity(searchCriteria);
		return getEntityListByEntityCriteria(persistedEntity);
	}

	public <D extends BaseDTO> List<E> getEntityListForUpdateByDTOCriteria(D searchCriteria, Class converterClass)
	{
		E persistedEntity = (E) getRelevantConverter(converterClass).convertToEntity(searchCriteria);
		return getEntityListForUpdateByEntityCriteria(persistedEntity);
	}
	
	public <D extends BaseDTO> List<D> updateDTOListReturnDTOListByDTOCriteria(D selectCriteria, D newParams, Class converterClass)
	{
		E entity = (E) getRelevantConverter(converterClass).convertToEntity(selectCriteria);
		E newValues = (E) getRelevantConverter(converterClass).convertToEntity(newParams);
		List<E> allEntities = getEntityListByEntityCriteria(entity);
		for (E be : allEntities)
		{
			callSettersOnObjectWithoutNullAndIdFields(be, newValues);
		}
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> D saveDTOReturnDTO(D baseDTO, Class converterClass)
	{
		Class<? extends BaseDTO> dtoClass = baseDTO.getClass();	
		E persistedEntity = (E) getRelevantConverter(converterClass).convertToEntity(baseDTO);
		//EnrichEntity
		entityManager.persist(persistedEntity);
		entityManager.flush();
		try
		{
			Field idField = dtoClass.getDeclaredField("id");
			Field daoField = persistedEntity.getClass().getDeclaredField("id");
			daoField.setAccessible(true);
			idField.setAccessible(true);
			idField.set(baseDTO, daoField.get(persistedEntity));
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e)
		{
			String exceptionMessage = "Exception using reflections in repository. Detail: " + ExceptionUtils.getStackTrace(e);
			log.info("An unexpected exception. See error log for details");
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.SAVE_DTO_RETURN_DTO_EXCEPTION, ExceptionUtils.getMessage(e) ,e);		
		}
		return baseDTO;
	}

	public <D extends BaseDTO> List<D> getDTOListByDTOCriteria(D searchCriteria, Class converterClass)
	{
		E persistedEntity = (E) getRelevantConverter(converterClass).convertToEntity(searchCriteria);
		List<E> allEntities = getEntityListByEntityCriteria(persistedEntity);
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> List<D> getDTOListForUpdateByDTOCriteria(D searchCriteria, Class converterClass)
	{
		BaseConverter bc = getRelevantConverter(converterClass);
		E persistedEntity = (E) bc.convertToEntity(searchCriteria);
		List<E> allEntities = getEntityListForUpdateByEntityCriteria(persistedEntity);
		return (List<D>) bc.mapListEntityToDTO(allEntities);
	}
	
	public <D extends BaseDTO, D2 extends BaseDTO> List<D2> updateDTOListByDTOCriteriaAndReturnAnotherDTOList(D searchCriteria, D newParams, Class returnClass, Class converterAsInputClass, Class converterToReturnClass)
	{
		BaseConverter bcInput = getRelevantConverter(converterAsInputClass);
		E entity = (E) bcInput.convertToEntity(searchCriteria);
		E newValues = (E) bcInput.convertToEntity(newParams);
		List<E> allEntities = getEntityListByEntityCriteria(entity);
		for (E be : allEntities)
		{
			callSettersOnObjectWithoutNullAndIdFields(be, newValues);
		}
		return (List<D2>) getRelevantConverter(converterToReturnClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO, D2 extends BaseDTO> D2 saveDTOReturnAnotherDTO(D baseDTO, Class returnClass, Class converterAsInputClass, Class converterToReturnClass)
	{
		E persistedEntity = (E) getRelevantConverter(converterAsInputClass).convertToEntity(baseDTO);
		//EnrichEntity
		entityManager.persist(persistedEntity);
		entityManager.flush();
		return (D2) getRelevantConverter(converterToReturnClass).convertToDTO(persistedEntity);
	}

	public <D extends BaseDTO> List<D> getDTOListByCriteria(Class dtoClass, CriteriaQuery<E> cq, Class converterClass)
	{
		TypedQuery<E> query = entityManager.createQuery(cq);
		List<E> allEntities = query.getResultList();		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> List<D> getDTOListByCriteriaWithPagination(Class dtoClass, CriteriaQuery<E> cq, Class converterClass, int first, int pageSize)
	{
		TypedQuery<E> query = entityManager.createQuery(cq);
		query.setFirstResult(first);
		query.setMaxResults(pageSize);
		List<E> allEntities = query.getResultList();		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	public <D extends BaseDTO> List<D> getDTOListForUpdateByCriteria(Class dtoClass, CriteriaQuery<E> cq, Class converterClass)
	{
		TypedQuery<E> query = entityManager.createQuery(cq);
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		List<E> allEntities = query.getResultList();		
		return (List<D>) getRelevantConverter(converterClass).mapListEntityToDTO(allEntities);
	}

	/* ***********************
	 *    Private Methods
	 *************************/
	
	private E saveEntityWithFlushOption(E baseEntity, boolean flushNeeded)
	{	
		entityManager.persist(baseEntity);
		if(flushNeeded)
		{
			entityManager.flush();
		}
		return baseEntity;
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
	
	// Ayni tipte objeler arasinda, property ismi "id" olmayan ve null olmayan tum property degerlerini (getter ve settera sahip olanlari) source dan destination a kopyalar. (mergeinsession true ise jpa sessiondaki obje ile merge edilir)
	private void setNotNullAndNotIdParametersOfSameType(Object source, Object destinationObjectOrCollection, boolean mergeInSessionFlag)
	{
		List<SetterAndValue> setterValuePairs = copyFromSourceToBuffer(source);
		if (destinationObjectOrCollection instanceof Collection)
		{
			Collection destination = (Collection) destinationObjectOrCollection;
			copyFromBufferToDestinationCollection(destination, setterValuePairs);
			if (mergeInSessionFlag)
			{
				for (Object item : destination)
				{
					entityManager.merge(item);
				}
				entityManager.flush();
			}
		}
		else// not a collection type.. assuming it as solely an object
		{
			copyFromBufferToDestinationObject(destinationObjectOrCollection, setterValuePairs);
			if (mergeInSessionFlag)
			{
				entityManager.merge(destinationObjectOrCollection);
			}
			entityManager.flush();
		}
	}

	// Copy from source to buffer
	private List<SetterAndValue> copyFromSourceToBuffer(Object source)
	{
		List<SetterAndValue> setterValuePairs = new ArrayList<>();
		Method getter = null;
		Method setter = null;
		try
		{
			BeanInfo info = Introspector.getBeanInfo(source.getClass(), Object.class);
			PropertyDescriptor[] props = info.getPropertyDescriptors();
			for (PropertyDescriptor pd : props)
			{
				getter = pd.getReadMethod();
				setter = pd.getWriteMethod();
				Object willChangeToValue = getter.invoke(source);// Which values
				// a non null field as new data
				if (willChangeToValue != null && !"id".equalsIgnoreCase(pd.getName()) && setter != null)// get
				{
					setterValuePairs.add(new SetterAndValue(setter, willChangeToValue));
				}
			}
		}
		catch (Exception e)
		{
			throw new ApplicationException(ExceptionConstants.SET_NOT_NULL_AND_NOT_ID_PARAMETERS_OF_SAME_TYPE_EXCEPTION, ExceptionUtils.getMessage(e), e);
		}
		return setterValuePairs;
	}

	// Copy from buffer to destination collection
	private boolean copyFromBufferToDestinationCollection(Collection destinationObjectOrCollection, List<SetterAndValue> buffer)
	{
		if (destinationObjectOrCollection == null)
		{
			return true;
		}
		Iterator iterator = destinationObjectOrCollection.iterator();
		while (iterator.hasNext())
		{
			Object item = iterator.next();
			for (SetterAndValue sv : buffer)
			{
				try
				{
					sv.setter.invoke(item, sv.value);
				}
				catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
				{
					throw new ApplicationException(ExceptionConstants.COPY_FROM_BUFFER_TO_DESTINATION_COLLECTION_EXCEPTION, ExceptionUtils.getMessage(e), e);
				}
			}
		}
		return true;
	}

	// Copy from buffer to destination object
	private boolean copyFromBufferToDestinationObject(Object destination, List<SetterAndValue> buffer)
	{
		for (SetterAndValue sv : buffer)
		{
			try
			{
				Class clazz = destination.getClass();
				sv.setter.invoke(clazz.cast(destination), sv.value);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				throw new ApplicationException(ExceptionConstants.COPY_FROM_BUFFER_TO_DESTINATION_OBJECT_EXCEPTION, ExceptionUtils.getMessage(e), e);
			}
		}
		return true;
	}
	
	private <D extends BaseDTO> BaseConverter<E,D> getRelevantConverter(Class<? extends BaseConverter<E,D>> mappingClass)
	{
		return null;
	}
	
	// Returns ENTITY version of the DTO that has an id attribute and not null id value.
	private <D extends BaseDTO> E getEntityUsingDTOWithId(D dto)
	{
		Class<? extends BaseDTO> bdType = dto.getClass();
		Method method = null;
		Number id = null;
		try
		{
			method = bdType.getMethod(Constants.ID_GETTER_KEY);
			id = (Number) method.invoke(dto);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
		{
			throw new ApplicationException(ExceptionConstants.GET_ENTITY_USING_DTO_WITH_ID_EXCEPTION, ExceptionUtils.getMessage(e), e);
		}
		return getEntityForUpdate(id);
	}

	// Copies from not null and not id values of newValues to be object
	private <E2 extends BaseEntity> void callSettersOnObjectWithoutNullAndIdFields(E2 be, E2 newValues)
	{
		Class beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		for (Field field : beType.getDeclaredFields())
		{
			field.setAccessible(true);
			try
			{
				if (field.get(be) != null && !"id".equalsIgnoreCase(field.getName()) && field.get(newValues) != null)
				{
					field.set(be, field.get(newValues));
				}
			}
			catch (IllegalArgumentException | IllegalAccessException e)
			{
				String exceptionMessage = "Exception calling reflections in repository. Detail: " + ExceptionUtils.getStackTrace(e);
				log.info("An unexpected exception. See error log for details");
				log.error(exceptionMessage);
								
				throw new ApplicationException(ExceptionConstants.CALL_SETTERS_ON_OBJECT_WITHOUT_NULL_AND_ID_FIELDS_EXCEPTION, ExceptionUtils.getMessage(e), e);
			}
		}
	}

	private class SetterAndValue
	{
		Method setter;
		Object value;

		SetterAndValue(Method setter, Object value)
		{
			this.setter = setter;
			this.value = value;
		}
	}
}