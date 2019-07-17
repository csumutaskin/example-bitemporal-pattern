package tr.com.poc.temporaldate.bitemporalexample.dto.converter.bitemporalorganization;

import java.lang.reflect.ParameterizedType;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateResponseDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.converter.BaseBitemporalConverter;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.util.Trim;

/**
 * Custom converter between {@link BitemporalOrganization} and {@link BitemporalOrganizationSaveOrUpdateResponseDTO} objects
 * 
 * @author umutaskin
 */
@Component
@Log4j2
public class BitemporalOrganizationSaveOrUpdateResponseDTOConverter  extends BaseBitemporalConverter<BitemporalOrganization, BitemporalOrganizationSaveOrUpdateResponseDTO>
{
	@Override
	public BitemporalOrganization convertDTOToEntity(BitemporalOrganizationSaveOrUpdateResponseDTO bd) 
	{
		Class<?> beType = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<?> bdType = (Class<?>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
		throw new ApplicationException(ExceptionConstants.NOT_CONVERTABLE_ENTITY_EXCEPTION, beType.getSimpleName(), bdType.getSimpleName());
	}

	/**
	 * Converts {@link BitemporalOrganization} entity to {@link BitemporalOrganizationSaveOrUpdateResponseDTO}
	 */
	@Override
	public BitemporalOrganizationSaveOrUpdateResponseDTO convertEntityToDTO(BitemporalOrganization be) 
	{
		if(be == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		BitemporalOrganizationSaveOrUpdateResponseDTO toReturn = new BitemporalOrganizationSaveOrUpdateResponseDTO(be.getName(), be.getOrgId(), be.getFineAmount(), be.getEarnAmount());
		this.enrichDTODates(toReturn, be);
		log.debug(() -> be.toString() + " object converted to " + toReturn.toString());
		return toReturn;
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
