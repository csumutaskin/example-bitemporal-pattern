package tr.com.poc.temporaldate.bitemporalexample.dto.converter.bitemporalorganization;

import java.lang.reflect.ParameterizedType;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.converter.BaseBitemporalConverter;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.util.Trim;

/**
 * Custom converter between {@link BitemporalOrganization} and {@link BitemporalOrganizationSaveOrUpdateRequestDTO} objects
 * 
 * @author umutaskin
 */
@Component
@Log4j2
public class BitemporalOrganizationSaveOrUpdateRequestDTOConverter  extends BaseBitemporalConverter<BitemporalOrganization, BitemporalOrganizationSaveOrUpdateRequestDTO>
{
	@Override
	public BitemporalOrganization convertDTOToEntity(BitemporalOrganizationSaveOrUpdateRequestDTO bd) 
	{
		if(bd == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		BitemporalOrganization toReturn = new BitemporalOrganization(null, bd.getName(), null, bd.getFineAmount(), bd.getEarnAmount());
		toReturn.setEffectiveDateStart(bd.getEffectiveDateStart());
		toReturn.setEffectiveDateEnd(bd.getEffectiveDateEnd());
		log.debug(() -> bd.toString() + " object converted to " + toReturn.toString());
		return toReturn;	
	}

	/**
	 * Converts {@link BitemporalOrganization} entity to {@link BitemporalOrganizationSaveOrUpdateRequestDTO}
	 */
	@Override
	public BitemporalOrganizationSaveOrUpdateRequestDTO convertEntityToDTO(BitemporalOrganization be) 
	{
		Class<?> beType = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<?> bdType = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		throw new ApplicationException(ExceptionConstants.NOT_CONVERTABLE_ENTITY_EXCEPTION, beType.getSimpleName(), bdType.getSimpleName());
	}

	/**
	 * Trims all Effective Begin Types assigned using this converter utility 
	 */
	@Override
	public Trim setEffectiveBeginDateTrimType() 
	{
		return null;
	}
}
