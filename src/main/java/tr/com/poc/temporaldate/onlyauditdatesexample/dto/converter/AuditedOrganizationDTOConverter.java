package tr.com.poc.temporaldate.onlyauditdatesexample.dto.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.Level;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.util.StringUtils;
import tr.com.poc.temporaldate.onlyauditdatesexample.model.AuditedOrganization;
import tr.com.poc.temporaldate.temporalexample.dto.OrganizationDTO;
import tr.com.poc.temporaldate.temporalexample.model.Organization;

@Component
@Log4j2
public class AuditedOrganizationDTOConverter  implements BaseConverter<AuditedOrganization, OrganizationDTO>
{
	@Override
	public AuditedOrganization convertToEntity(OrganizationDTO bd) 
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
		return new AuditedOrganization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());	
	}

	@Override
	public OrganizationDTO convertToDTO(AuditedOrganization be) 
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

	@Override
	public Collection<OrganizationDTO> convertEntityCollectionToDTOCollection(Collection<AuditedOrganization> entityList) 
	{
		if(CollectionUtils.isEmpty(entityList))
		{
			log.debug("empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<OrganizationDTO> toReturn = new ArrayList<>();
		for(AuditedOrganization entity : entityList)
		{
			toReturn.add(convertToDTO(entity));
		}
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug("VersionedOrganization list converted to OrganizationDTO Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		}
		return toReturn;
	}

	@Override
	public Collection<AuditedOrganization> convertDTOCollectiontoEntityCollection(Collection<OrganizationDTO> dtoList) 
	{
		if(CollectionUtils.isEmpty(dtoList))
		{
			log.debug("empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<AuditedOrganization> toReturn = new ArrayList<>();
		for(OrganizationDTO dto : dtoList)
		{
			toReturn.add(convertToEntity(dto));
		}
		if(log.isEnabled(Level.DEBUG))
		{
			log.debug("OrganizationDTO list converted to VersionedOrganization Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		}
		return toReturn;
	}
}
