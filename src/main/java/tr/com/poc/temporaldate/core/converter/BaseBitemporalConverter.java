package tr.com.poc.temporaldate.core.converter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.Level;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.BaseBitemporalDTO;
import tr.com.poc.temporaldate.core.model.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.util.comparator.DateUtils;
import tr.com.poc.temporaldate.util.ExceptionConstants;
import tr.com.poc.temporaldate.util.StringUtils;

/**
 * A base converter that automates "record date" and "effective date" operations on bi-temporal objects' conversion
 * 
 * @author umut
 *
 * @param <E> any class that extends {@link BaseBitemporalEntity}
 * @param <D> any class that extends {@link BaseBitemporalDTO}
 */
@SuppressWarnings("unchecked")
@Log4j2
public abstract class BaseBitemporalConverter<E extends BaseBitemporalEntity, D extends BaseBitemporalDTO> implements BaseConverter<E,D> 
{
	private static final boolean NOW_OR_START_DAY_OF_MONTH_FLAG = false; //true: now is current date, false now is the beginning day of current month
	
	public abstract E convertDTOToEntity(D bd);
	public abstract D convertEntityToDTO(E be);
	
	public E enrichEntityRecordDates(E entityToEnrich, Date now)
	{		
		if(now == null)
		{
			now = DateUtils.getNowOrOpenPeriodStartDate(NOW_OR_START_DAY_OF_MONTH_FLAG);
		}
		entityToEnrich = initializeObjectIfNull(entityToEnrich);			
		entityToEnrich.setRecordDateStart(now);
		entityToEnrich.setRecordDateEnd(DateUtils.END_OF_EPYS);
		return entityToEnrich;
	}
	
	public E enrichEntityEffectiveDates(E entityToEnrich, Date now)
	{		
		if(now == null)
		{
			now = DateUtils.getNowOrOpenPeriodStartDate(NOW_OR_START_DAY_OF_MONTH_FLAG);
		}
		entityToEnrich = initializeObjectIfNull(entityToEnrich);	
		if(entityToEnrich.getEffectiveDateStart() == null)
		{
			entityToEnrich.setEffectiveDateStart(now);
		}
		if(entityToEnrich.getEffectiveDateEnd() == null)
		{
			entityToEnrich.setEffectiveDateEnd(DateUtils.END_OF_EPYS);
		}
		return entityToEnrich;
	}
	
	public D enrichDTOEffectiveDates(D dtoToEnrich, E sourceEntity)
	{		
		if(sourceEntity == null)//null is expected on enriched date columns so just return the object
		{
			log.info("while enriching DTO, since source entity is null, dto will return immediately without setting any values. DTO is: {}", dtoToEnrich);
			return dtoToEnrich;
		}
		dtoToEnrich = initializeObjectIfNull(dtoToEnrich);	
		if(dtoToEnrich.getEffectiveDateStart() == null)
		{
			dtoToEnrich.setEffectiveDateStart(sourceEntity.getEffectiveDateStart());
		}
		if(dtoToEnrich.getEffectiveDateEnd() == null)
		{
			dtoToEnrich.setEffectiveDateEnd(sourceEntity.getEffectiveDateEnd());
		}
		return dtoToEnrich;
	}
	
	public E convertToEntity(D bd)
	{
		E toReturn = convertDTOToEntity(bd);
		Date now = DateUtils.getNowOrOpenPeriodStartDate(NOW_OR_START_DAY_OF_MONTH_FLAG);
		toReturn = enrichEntityRecordDates(toReturn, now);
		toReturn = enrichEntityEffectiveDates(toReturn, now);		
		return toReturn;
	}
	
	public D convertToDTO(E be)
	{
		D toReturn = convertEntityToDTO(be);
		toReturn = enrichDTOEffectiveDates(toReturn, be);
		return toReturn;
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
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug("Bi-temporal Entity collection converted to Bi-temporal DTO Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		}
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
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug("Bi-temporal DTO list converted to Bi-temporal Entity Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		}
		return toReturn;		
	}
	
	private E initializeObjectIfNull(E toInitialize)
	{		
		E toReturn = toInitialize;
		if(toReturn != null)
		{
			return toReturn;
		}
		Class<E> beType = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<?> clazz;
		try 
		{
			clazz = Class.forName(beType.getName());
			toReturn = (E)clazz.getDeclaredConstructor().newInstance();
		} 
		catch (Exception e) 
		{
			String exceptionMessage = "Exception in creating an instance of a null given entity object in a converter. Detail is: " + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.INITIALIZING_NULL_ENTITY_USING_REFLECTION_EXCEPTION, ExceptionUtils.getMessage(e) ,e);				
		}
		return toReturn;
	}
	
	private D initializeObjectIfNull(D toInitialize)
	{		
		D toReturn = toInitialize;
		if(toReturn != null)
		{
			return toReturn;
		}
		Class<D> bdType = (Class<D>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		Class<?> clazz;
		try 
		{
			clazz = Class.forName(bdType.getName());
			toReturn = (D)clazz.getDeclaredConstructor().newInstance();
		} 
		catch (Exception e) 
		{
			String exceptionMessage = "Exception in creating an instance of a null given DTO object in a converter. Detail is: " + ExceptionUtils.getStackTrace(e);	
			log.error(exceptionMessage);
			throw new ApplicationException(ExceptionConstants.INITIALIZING_NULL_DTO_USING_REFLECTION_EXCEPTION, ExceptionUtils.getMessage(e) ,e);				
		}
		return toReturn;
	}
}
