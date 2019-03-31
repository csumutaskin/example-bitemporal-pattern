package tr.com.poc.temporaldate.core.converter;

import java.util.Collection;

import tr.com.poc.temporaldate.core.model.BaseDTO;
import tr.com.poc.temporaldate.core.model.BaseEntity;

public interface BaseConverter<E extends BaseEntity,D extends BaseDTO> 
{
	E convertToEntity(D bd);	
	D convertToDTO(E be);
	Collection<D> convertEntityCollectionToDTOCollection(Collection<E> entityList);
	Collection<E> convertDTOCollectiontoEntityCollection(Collection<D> dtoList);	
}

