package tr.com.poc.temporaldate.bitemporalexample.dto.converter;

import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.core.converter.BaseBitemporalConverter;
import tr.com.poc.temporaldate.core.util.Trim;

/**
 * Custom converter between {@link BitemporalOrganization} and {@link BitemporalOrganizationDTO} objects
 * 
 * @author umutaskin
 */
@Component
@Log4j2
public class BitemporalOrganizationDTOConverter  extends BaseBitemporalConverter<BitemporalOrganization, BitemporalOrganizationDTO>
{
	@Override
	public BitemporalOrganization convertDTOToEntity(BitemporalOrganizationDTO bd) 
	{
		if(bd == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		BitemporalOrganization toReturn = new BitemporalOrganization(null, bd.getName(), bd.getOrgId(), bd.getFineAmount(), bd.getEarnAmount());
		toReturn.setEffectiveDateStart(bd.getEffectiveDateStart());
		toReturn.setEffectiveDateEnd(bd.getEffectiveDateEnd());
		log.debug(() -> bd.toString() + " object converted to " + toReturn.toString());
		return toReturn;	
	}

	@Override
	public BitemporalOrganizationDTO convertEntityToDTO(BitemporalOrganization be) 
	{
		if(be == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		BitemporalOrganizationDTO toReturn = new BitemporalOrganizationDTO(be.getName(), be.getOrgId(), be.getFineAmount(), be.getEarnAmount());
		this.enrichDTODates(toReturn, be);
		log.debug(() -> be.toString() + " object converted to " + toReturn.toString());
		return toReturn;
	}

	@Override
	public Trim setEffectiveBeginDateTrimType() 
	{
		return null;
	}
}
