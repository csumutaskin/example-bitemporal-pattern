package tr.com.poc.temporaldate.bitemporalexample.dto.converter;

import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.core.converter.BaseBitemporalConverter;

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
		BitemporalOrganization toReturn = new BitemporalOrganization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());
		toReturn.setEffectiveDateStart(bd.getEffectiveDateStart());
		toReturn.setEffectiveDateEnd(bd.getEffectiveDateEnd());
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug(bd.toString() + " object converted to " + toReturn.toString());
		}
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
		return false;
	}
}
