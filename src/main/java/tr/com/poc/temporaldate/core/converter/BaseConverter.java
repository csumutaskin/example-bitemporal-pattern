package tr.com.poc.temporaldate.core.converter;

import java.util.Collection;

import tr.com.poc.temporaldate.core.model.BaseDTO;
import tr.com.poc.temporaldate.core.model.BaseEntity;

/**
 * Converter interface needed to convert an entity to a DTO and vice versa...
 * 
 * @author umutaskin
 *
 * @param <E> any class that extends {@link BaseEntity}
 * @param <D> any class that extends {@link BaseDTO}
 */
public interface BaseConverter<E extends BaseEntity,D extends BaseDTO> 
{
	E convertToEntity(D bd);	
	D convertToDTO(E be);
	Collection<D> convertEntityCollectionToDTOCollection(Collection<E> entityList);
	Collection<E> convertDTOCollectiontoEntityCollection(Collection<D> dtoList);	
}

