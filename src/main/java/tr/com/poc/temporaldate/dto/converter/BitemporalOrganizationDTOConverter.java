package tr.com.poc.temporaldate.dto.converter;

import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.converter.BaseBitemporalConverter;
import tr.com.poc.temporaldate.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.model.BitemporalOrganization;
import tr.com.poc.temporaldate.model.Organization;

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
		Organization toReturn = new Organization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug(bd.toString() + " object converted to " + toReturn.toString());
		}
		return new BitemporalOrganization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());	
	}

	@Override
	public BitemporalOrganizationDTO convertEntityToDTO(BitemporalOrganization be) 
	{
		if(be == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		BitemporalOrganizationDTO toReturn = new BitemporalOrganizationDTO(be.getName(), be.getShortName(), be.getFineAmount(), be.getEarnAmount());
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug(be.toString() + " object converted to " + toReturn.toString());
		}
		return toReturn;
	}

	@Override
	public boolean overrideEffectiveStartToBeginPeriodAlways() 
	{
		return true;
	}

	@Override
	public boolean overrideEffectiveEndToEndofSoftware() 
	{
		return true;
	}
}
