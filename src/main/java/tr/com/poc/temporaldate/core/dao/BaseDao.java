package tr.com.poc.temporaldate.core.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.model.BaseDTO;
import tr.com.poc.temporaldate.core.model.BaseEntity;

public interface BaseDao<E extends BaseEntity> 
{
	EntityManager getEntityManager();
	void setEntityManager(EntityManager entityManager);
	List<E> getEntityList();
	List<E> getEntityListForUpdate();
	E getEntity(final Serializable pk);
	E getEntityForUpdate(final Serializable pk);
	E updateEntity(E toUpdate);
	boolean deleteEntityListByEntityCriteria(E toDeleteFilter);
	E saveAndFlushEntity(E baseEntity, boolean flushNeeded);
	E saveEntity(E baseEntity);
    void deleteEntity(E baseEntity);
	void deleteEntityList(List<E> entityList);
	List<E> getEntityListByEntityCriteria(E searchCriteria);
	Long getRowCountByEntityCriteria(E searchCriteria);
	List<E> getEntityListByEntityCriteriaWithPagination(E searchCriteria, int first, int pageSize);
	E getEntityByEntityCriteria(E searchCriteria);
	E getEntityForUpdateByEntityCriteria(E searchCriteria);
	List<E> getEntityListByEntityORCriteria(E searchCriteria);
	List<E> getEntityListForUpdateByEntityCriteria(E searchCriteria);
	Long getCount();
	List<E> getEntityListByNamedQuery(String namedQueryName, Object... parameters);
	List<E> getEntityListForUpdateByNamedQuery(String namedQueryName, Object... parameters);
	int runNamedQuery(String namedQueryName, Object... parameters);
	E getEntityByNamedQuery(String namedQueryName, Object... parameters);
	E getEntityForUpdateByNamedQuery(String namedQueryName, Object... parameters);
	boolean batchInsert(Collection<E> collection);
	boolean batchUpdate(Collection<E> collection);
	<D extends BaseDTO> List<D> getDTOList(Class<?> dtoClassToReturn, Class<? extends BaseConverter<E,D>> converterClass);
	<D extends BaseDTO> List<D> getDTOListForUpdate(Class<?> dtoClassToReturn, Class<?> converterClass);
	<D extends BaseDTO> D updateEntityReturnDTO(Class<?> dtoClassToReturn, E toUpdate, Class<?> converterClass);
	<D extends BaseDTO> D getEntityById(Class<?> dtoClassToReturn, Long pk, Class<?> converterClass);
	<D extends BaseDTO> D saveEntityReturnDTO(Class<?> dtoClassToReturn, E peristedEntity, Class<?> converterClass);
	<D extends BaseDTO> List<D> getDTOListByEntityCriteria(Class<?> dtoClassToReturn, E searchCriteria, Class<?> converterClass);
	<D extends BaseDTO> List<D> getDTOListByEntityCriteriaWithPagination(Class<?> dtoClassToReturn, E searchCriteria, Class<?> converterClass, int firstResult, int pageSize);
	<D extends BaseDTO> List<D> getDTOListForUpdateByEntityCriteria(Class<?> dtoClassToReturn, E searchCriteria, Class<?> converterClass);
	<D extends BaseDTO> List<D> getDTOListByNamedQuery(Class<?> dtoClassToReturn, String namedQueryName, Class<?> converterClass, Object... parameters);
	<D extends BaseDTO> List<D> getDTOListForUpdateByNamedQuery(Class<?> dtoClassToReturn, String namedQueryName, Class<?> converterClass, Object... parameters);
	<D extends BaseDTO> D getDTOByNamedQuery(Class<?> dtoClassToReturn, String namedQueryName, Class<?> converterClass, Object... parameters);
	<D extends BaseDTO> D getDTOForUpdateByNamedQuery(Class<?> dtoClassToReturn, String namedQueryName, Class<?> converterClass, Object... parameters);
	<D extends BaseDTO> E updateDTOByDtoIDCriteriaReturnEntity(D idSearchCriteria, Class<?> converterClass);
	<D extends BaseDTO> E saveDTOReturnEntity(D baseDTO, Class<?> converterClass);
	<D extends BaseDTO> E deleteDTOReturnDeletedEntity(D baseDTO);
	<D extends BaseDTO> List<E> getEntityListByDTOCriteria(D searchCriteria, Class<?> converterClass);
	<D extends BaseDTO> List<E> getEntityListForUpdateByDTOCriteria(D searchCriteria, Class<?> converterClass);
	<D extends BaseDTO> List<D> updateDTOListReturnDTOListByDTOCriteria(D selectCriteria, D newParams, Class<?> converterClass);
	<D extends BaseDTO> D saveDTOReturnDTO(D baseDTO, Class<?> converterClass);
	<D extends BaseDTO> List<D> getDTOListByDTOCriteria(D searchCriteria, Class<?> converterClass);
	<D extends BaseDTO> List<D> getDTOListForUpdateByDTOCriteria(D searchCriteria, Class<?> converterClass);
	<D extends BaseDTO, D2 extends BaseDTO> List<D2> updateDTOListByDTOCriteriaAndReturnAnotherDTOList(D searchCriteria, D newParams, Class<?> returnClass, Class<?> converterAsInputClass, Class<?> converterToReturnClass);
	<D extends BaseDTO, D2 extends BaseDTO> D2 saveDTOReturnAnotherDTO(D baseDTO, Class<?> returnClass, Class<?> converterAsInputClass, Class<?> converterToReturnClass);
	<D extends BaseDTO> List<D> getDTOListByCriteria(Class<?> dtoClass, CriteriaQuery<E> cq, Class<?> converterClass);
	<D extends BaseDTO> List<D> getDTOListByCriteriaWithPagination(Class<?> dtoClass, CriteriaQuery<E> cq, Class<?> converterClass, int first, int pageSize);
	<D extends BaseDTO> List<D> getDTOListForUpdateByCriteria(Class<?> dtoClass, CriteriaQuery<E> cq, Class<?> converterClass);
}
