package tr.com.poc.temporaldate.core.converter;

import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.util.DateUtils;
import tr.com.poc.temporaldate.core.util.StringUtils;
import tr.com.poc.temporaldate.core.util.Trim;

/**
 * A base converter that automates "record date" and "effective date" operations on bi-temporal objects' conversion
 * 
 * @author umutaskin
 *
 * @param <E> any class that extends {@link BaseBitemporalEntity}
 * @param <D> any class that extends {@link BaseBitemporalDTO}
 */
@SuppressWarnings("unchecked")
@Log4j2
public abstract class BaseBitemporalConverter<E extends BaseBitemporalEntity, D extends BaseBitemporalDTO> implements BaseConverter<E,D> 
{
	/**
	 * While converting an entity to DTO:
	 * If set, trim type is used to round effective begin date of a tuple w.r.t current time,
	 * if effective begin date is manually set by user on DTO, no trim rule is applied to now to find effective begin date.
	 * @return boolean to calculate the effective begin date
	 */
	public abstract Trim setEffectiveBeginDateTrimType();	
	
	/**
	 * Converts DTO object to the related Entity object
	 * @param bd {@link BaseBitemporalDTO} to be converted
	 * @return related {@link BaseBitemporalEntity} that is converted successfully
	 */
	public abstract E convertDTOToEntity(D bd);
	
	
	/**
	 * Converts Entity object to the related DTO object to be served in controller/service layer
	 * @param be {@link BaseBitemporalEntity} to be converted
	 * @return related {@link BaseBitemporalDTO} that is converted successfully
	 */
	public abstract D convertEntityToDTO(E be);
			
	/**
	 * Sets DTO's effective start and end dates using the entities related date columns
	 * 
	 * @param entityThatWillEnrich object used to enrich dto
	 */
	public void enrichDTODates(D dtoToEnrich, E entityThatWillEnrich)
	{
		dtoToEnrich.setEffectiveDateStart(entityThatWillEnrich.getEffectiveDateStart());
		dtoToEnrich.setEffectiveDateEnd(entityThatWillEnrich.getEffectiveDateEnd());		
	}
	
	/**
	 * Converts a {@link BaseBitemporalDTO} to {@link BaseBitemporalEntity} using the flow implemented in abstract convertDTOToEntity method
	 * @return {@link BaseBitemporalEntity} that is converted to 
	 */
	public final E convertToEntity(D bd)
	{
		if(bd == null)
		{
			return null;	
		}
		E toReturn = convertDTOToEntity(bd);
		toReturn.setIsDeleted(Boolean.FALSE);
		LocalDateTime currentNow = LocalDateTime.now();
		LocalDateTime effectiveNow = currentNow;
		Trim trimType = setEffectiveBeginDateTrimType();
		if(trimType != null && trimType != Trim.NOW)//override now
		{
			effectiveNow = DateUtils.getNowOrGivenOrOpenPeriodStartDate(trimType);
		}
		enrichEntityPerspectiveDates(toReturn, currentNow);
		return enrichEntityEffectiveDates(toReturn, effectiveNow);		
	}
	
	/**
	 * Converts a {@link BaseBitemporalDTO} to {@link BaseBitemporalEntity} using the flow implemented in abstract convertDTOToEntity method
	 * @return {@link BaseBitemporalEntity} that is converted to 
	 */
	public final D convertToDTO(E be)
	{
		return convertEntityToDTO(be);
	}
	
	public Collection<D> convertEntityCollectionToDTOCollection(Collection<E> entityList)
	{
		if(CollectionUtils.isEmpty(entityList))
		{
			log.debug("Empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<D> toReturn = new ArrayList<>();
		for(E entity : entityList)
		{
			toReturn.add(convertToDTO(entity));
		}
		log.debug(() -> "Bitemporal Entity collection converted to Bitemporal DTO Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		return toReturn;		
	}
	
	/**
	 * Converts given DTO collection to the related entity collection one by one
	 * @param dtoList dtolist to be converted to
	 */
	public Collection<E> convertDTOCollectiontoEntityCollection(Collection<D> dtoList)
	{
		if(CollectionUtils.isEmpty(dtoList))
		{
			log.debug("Empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<E> toReturn = new ArrayList<>();
		for(D dto : dtoList)
		{
			toReturn.add(convertToEntity(dto));
		}
		log.debug(() -> "Bitemporal DTO list converted to Bitemporal Entity Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		return toReturn;		
	}
	
	/*
	 * Initializes a new object using reflection
	 */
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
			throw new ApplicationException(ExceptionConstants.INITIALIZING_NULL_ENTITY_USING_REFLECTION_EXCEPTION, e);				
		}
		return toReturn;
	}
	
	/*
	 * If perspective dates are still null on entity after convertDTOtoEntity method is called, 
	 * this method fills as below
	 * 
	 * perspective begin: "now" 
	 * perspective end: end of software 
	 */	
	private void enrichEntityPerspectiveDates(E entityToEnrich, LocalDateTime now)
	{		
		entityToEnrich = initializeObjectIfNull(entityToEnrich);
		if(entityToEnrich.getPerspectiveDateStart() == null)
		{
			if(now == null)
			{
				now = LocalDateTime.now();
			}
			entityToEnrich.setPerspectiveDateStart(now);
		}
		if(entityToEnrich.getPerspectiveDateEnd() == null)
		{
			entityToEnrich.setPerspectiveDateEnd(DateUtils.END_OF_SOFTWARE);
		}
	}
	
	/*
	 * If effective dates are still null on entity after convertDTOtoEntity method is called, 
	 * this method fills as below
	 * 
	 * effective begin: "now" or end of period according to overridden getNowOrGivenOrOpenPeriodStartDate() method's return value
	 * effective end: end of software 
	 */	
	private E enrichEntityEffectiveDates(E entityToEnrich, LocalDateTime now)
	{			
		entityToEnrich = initializeObjectIfNull(entityToEnrich);	
		if(entityToEnrich.getEffectiveDateStart() == null)
		{
			if(now == null)
			{
				now = DateUtils.getNowOrGivenOrOpenPeriodStartDate(setEffectiveBeginDateTrimType());
			}
			entityToEnrich.setEffectiveDateStart(now);
		}
		if(entityToEnrich.getEffectiveDateEnd() == null)
		{
			entityToEnrich.setEffectiveDateEnd(DateUtils.END_OF_SOFTWARE);
		}
		return entityToEnrich;
	}
}
