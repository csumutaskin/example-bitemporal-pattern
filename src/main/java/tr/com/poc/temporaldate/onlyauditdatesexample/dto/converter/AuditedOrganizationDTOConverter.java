package tr.com.poc.temporaldate.onlyauditdatesexample.dto.converter;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.converter.BaseConverter;
import tr.com.poc.temporaldate.core.util.StringUtils;
import tr.com.poc.temporaldate.onlyauditdatesexample.model.AuditedOrganization;
import tr.com.poc.temporaldate.temporalexample.dto.TemporalOrganizationDTO;
import tr.com.poc.temporaldate.temporalexample.model.TemporalOrganization;

@Component
@Log4j2
public class AuditedOrganizationDTOConverter  implements BaseConverter<AuditedOrganization, TemporalOrganizationDTO>
{
	@Override
	public AuditedOrganization convertToEntity(TemporalOrganizationDTO bd) 
	{		
		if(bd == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		TemporalOrganization toReturn = new TemporalOrganization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());
		log.debug(() -> bd.toString() + " object converted to " + toReturn.toString());
		return new AuditedOrganization(null, bd.getName(), bd.getShortName(), bd.getFineAmount(), bd.getEarnAmount());	
	}

	@Override
	public TemporalOrganizationDTO convertToDTO(AuditedOrganization be) 
	{
		if(be == null)
		{
			log.debug("null object converted to null");
			return null;
		}
		TemporalOrganizationDTO toReturn = new TemporalOrganizationDTO(be.getName(), be.getShortName(), be.getFineAmount(), be.getEarnAmount());
		log.debug(() -> be.toString() + " object converted to " + toReturn.toString());
		return toReturn;
	}

	@Override
	public Collection<TemporalOrganizationDTO> convertEntityCollectionToDTOCollection(Collection<AuditedOrganization> entityList) 
	{
		if(CollectionUtils.isEmpty(entityList))
		{
			log.debug("empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<TemporalOrganizationDTO> toReturn = new ArrayList<>();
		for(AuditedOrganization entity : entityList)
		{
			toReturn.add(convertToDTO(entity));
		}
		log.debug(() -> "VersionedOrganization list converted to OrganizationDTO Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		return toReturn;
	}

	@Override
	public Collection<AuditedOrganization> convertDTOCollectiontoEntityCollection(Collection<TemporalOrganizationDTO> dtoList) 
	{
		if(CollectionUtils.isEmpty(dtoList))
		{
			log.debug("empty collection converted to new Arraylist");
			return new ArrayList<>();
		}
		Collection<AuditedOrganization> toReturn = new ArrayList<>();
		for(TemporalOrganizationDTO dto : dtoList)
		{
			toReturn.add(convertToEntity(dto));
		}
		log.debug(() -> "OrganizationDTO list converted to VersionedOrganization Arraylist with contents: " + StringUtils.toStringCollection(toReturn));
		return toReturn;
	}
}
