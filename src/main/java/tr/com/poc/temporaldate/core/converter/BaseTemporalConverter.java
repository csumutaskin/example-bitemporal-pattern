package tr.com.poc.temporaldate.core.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Level;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.model.temporal.BaseTemporalDTO;
import tr.com.poc.temporaldate.core.model.temporal.BaseTemporalEntity;
import tr.com.poc.temporaldate.core.util.StringUtils;

/**
 * Converter that can be used for temporal entities and temporal DTO conversion to each other
 * @author umutaskin
 *
 * @param <E> any class that extends {@link BaseTemporalEntity}
 * @param <D> any class that extends {@link BaseTemporalDTO}
 */
@Log4j2
public abstract class BaseTemporalConverter<E extends BaseTemporalEntity, D extends BaseTemporalDTO> implements BaseConverter<E,D> 
{	
	public abstract E convertDTOToEntity(D bd);
	public abstract D convertEntityToDTO(E be);
	
	public E convertToEntity(D bd)
	{
		return convertDTOToEntity(bd);	
	}
	
	public D convertToDTO(E be)
	{
		return convertEntityToDTO(be);
	}
	
	public Collection<D> convertEntityCollectionToDTOCollection(Collection<E> entityList)
	{
		if(CollectionUtils.isEmpty(entityList))
		{
			log.debug("empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<D> toReturn = new ArrayList<>();
		for(E entity : entityList)
		{
			toReturn.add(convertToDTO(entity));
		}
		log.debug(() -> "Temporal Entity collection converted to Temporal DTO Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		return toReturn;		
	}
	
	public Collection<E> convertDTOCollectiontoEntityCollection(Collection<D> dtoList)
	{
		if(CollectionUtils.isEmpty(dtoList))
		{
			log.debug("empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<E> toReturn = new ArrayList<>();
		for(D dto : dtoList)
		{
			toReturn.add(convertToEntity(dto));
		}
		log.debug(() -> "Temporal DTO list converted to Temporal Entity Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		return toReturn;		
	}
}
