package tr.com.poc.temporaldate.temporalexample.dto.converter;

import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.converter.BaseTemporalConverter;
import tr.com.poc.temporaldate.temporalexample.dto.TemporalOrganizationDTO;
import tr.com.poc.temporaldate.temporalexample.model.TemporalOrganization;

@Component
@Log4j2
public class TemporalOrganizationDTOConverter extends BaseTemporalConverter<TemporalOrganization, TemporalOrganizationDTO>
{
	@Override
	public TemporalOrganization convertDTOToEntity(TemporalOrganizationDTO bd) 
	{
		if(bd == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		TemporalOrganization toReturn = new TemporalOrganization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug(bd.toString() + " object converted to " + toReturn.toString());
		}
		return new TemporalOrganization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());	
	}

	@Override
	public TemporalOrganizationDTO convertEntityToDTO(TemporalOrganization be) 
	{
		if(be == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		TemporalOrganizationDTO toReturn = new TemporalOrganizationDTO(be.getName(), be.getShortName(), be.getFineAmount(), be.getEarnAmount());
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug(be.toString() + " object converted to " + toReturn.toString());
		}
		return toReturn;
	}
}
