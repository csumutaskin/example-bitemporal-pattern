package tr.com.poc.temporaldate.core.converter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;
import tr.com.poc.temporaldate.core.util.DateUtils;
import tr.com.poc.temporaldate.core.util.StringUtils;

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
	 * <b>if true:</b> open period -currently the beginning day of month- is calculated, </br> <b>else:</b> now is used as effective start date in conversion process 
	 * @return boolean to calculate the effective begin date
	 */
	public abstract boolean overrideEffectiveStartToCurrentBeginPeriodAlways();
	
	/**
	 * This method indicates whether any tuple inserted to database through this converter should have an infinite effective date to the end of the software or not
	 * if true is returned in this method by the developer, any inserted tuple lasts till the end of the software (e.g. year "2100") before this end date is updated by any other insertion to the same table
	 * if false is returned developer take cares of the effective end date of the tuple
	 * Use it responsibly :) The risk totally belongs to you if you return "true" here... on the other hand if you want a full automation on effective end date for any inserted tuple, you can use it freely.
	 *   
	 * @return true if developer wants to always auto set the <b>"effectiveEndDate"</b> as infinitive, 
	 * false if it is always to be set manually by the developer, or wanted to be left as null in insertions after using the conversion utility of this converter 
	 */
	public abstract boolean overrideEffectiveEndToEndofSoftwareAlways(); 
	
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
	
	/*
	 * If record dates are still null on entity after convertDTOtoEntity method is called, 
	 * this method fills as below
	 * 
	 * perspective begin: "now" 
	 * perspective end: end of software 
	 */	
	private E enrichEntityRecordDates(E entityToEnrich, Date now)
	{		
		entityToEnrich = initializeObjectIfNull(entityToEnrich);
		if(entityToEnrich.getRecordDateStart() == null)
		{
			if(now == null)
			{
				now = new Date();
			}
			entityToEnrich.setRecordDateStart(now);
		}
		if(entityToEnrich.getRecordDateEnd() == null)
		{
			entityToEnrich.setRecordDateEnd(DateUtils.END_OF_SOFTWARE);
		}
		return entityToEnrich;
	}
	
	/*
	 * If effective dates are still null on entity after convertDTOtoEntity method is called, 
	 * this method fills as below
	 * 
	 * effective begin: "now" or end of period according to overridden getNowOrGivenOrOpenPeriodStartDate() method's return value
	 * effective end: end of software 
	 */	
	private E enrichEntityEffectiveDates(E entityToEnrich, Date now)
	{			
		entityToEnrich = initializeObjectIfNull(entityToEnrich);	
		if(entityToEnrich.getEffectiveDateStart() == null)
		{
			if(now == null || overrideEffectiveStartToCurrentBeginPeriodAlways())
			{
				now = DateUtils.getNowOrGivenOrOpenPeriodStartDate(!overrideEffectiveStartToCurrentBeginPeriodAlways());
			}
			entityToEnrich.setEffectiveDateStart(now);
		}
		if(entityToEnrich.getEffectiveDateEnd() == null || overrideEffectiveEndToEndofSoftwareAlways())
		{
			entityToEnrich.setEffectiveDateEnd(DateUtils.END_OF_SOFTWARE);
		}
		return entityToEnrich;
	}
	
	/**
	 * Sets dto's effective interval -if still null- by using the original entity's effective interval
	 * @param dtoToEnrich destination {@link BaseBitemporalDTO} object to be set
	 * @param sourceEntity source {@link BaseBitemporalEntity} to be converted from
	 * @return {@link BaseBitemporalDTO} given in method parameter set: 1st parameter enriched with entities effective interval
	 */
	public final D enrichDTOEffectiveDates(D dtoToEnrich, E sourceEntity)
	{		
		if(sourceEntity == null)//null is expected on enriched date columns so just return the object
		{
			log.info("while enriching DTO, since source entity is null, dto will return immediately without setting any values from converter. DTO is: {}", dtoToEnrich);
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
		Date now = new Date();
		toReturn = enrichEntityRecordDates(toReturn, now);
		toReturn = enrichEntityEffectiveDates(toReturn, now);		
		return toReturn;
	}
	
	/**
	 * Converts a {@link BaseBitemporalDTO} to {@link BaseBitemporalEntity} using the flow implemented in abstract convertDTOToEntity method
	 * @return {@link BaseBitemporalEntity} that is converted to 
	 */
	public final D convertToDTO(E be)
	{
		D toReturn = convertEntityToDTO(be);
		toReturn = enrichDTOEffectiveDates(toReturn, be);
		return toReturn;
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
			throw new ApplicationException(ExceptionConstants.INITIALIZING_NULL_DTO_USING_REFLECTION_EXCEPTION, e);				
		}
		return toReturn;
	}
}
