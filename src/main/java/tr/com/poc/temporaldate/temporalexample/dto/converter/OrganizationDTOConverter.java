package tr.com.poc.temporaldate.temporalexample.dto.converter;

import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.converter.BaseTemporalConverter;
import tr.com.poc.temporaldate.temporalexample.dto.OrganizationDTO;
import tr.com.poc.temporaldate.temporalexample.model.Organization;

@Component
@Log4j2
public class OrganizationDTOConverter extends BaseTemporalConverter<Organization, OrganizationDTO>
{
	@Override
	public Organization convertDTOToEntity(OrganizationDTO bd) 
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
		return new Organization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());	
	}

	@Override
	public OrganizationDTO convertEntityToDTO(Organization be) 
	{
		if(be == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		OrganizationDTO toReturn = new OrganizationDTO(be.getName(), be.getShortName(), be.getFineAmount(), be.getEarnAmount());
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug(be.toString() + " object converted to " + toReturn.toString());
		}
		return toReturn;
	}
}
