package tr.com.poc.temporaldate.bitemporalexample.dto.converter.bitemporaluser;

import org.springframework.stereotype.Component;

import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalUser;
import tr.com.poc.temporaldate.core.converter.BaseBitemporalConverter;
import tr.com.poc.temporaldate.core.util.Trim;

/**
 * Converter class between {@link BitemporalUser} and {@link BitemporalUserWithOrganizationDTO} objects 
 * 
 * @author umutaskin
 */
@Component
public class BitemporalUserWithOrganizationDTOConverter extends BaseBitemporalConverter<BitemporalUser, BitemporalUserWithOrganizationDTO> 
{
	@Override
	public Trim setEffectiveBeginDateTrimType() 
	{
		return null;
	}

	@Override
	public BitemporalUser convertDTOToEntity(BitemporalUserWithOrganizationDTO bd) 
	{
		if(bd == null)
		{
			return null;
		}
		BitemporalUser toReturn = new BitemporalUser();
		toReturn.setUserName(bd.getUserName());
		toReturn.setName(bd.getName());
		BitemporalOrganization organization = new BitemporalOrganization();
		organization.setEarnAmount(bd.getEarnAmount());
		organization.setFineAmount(bd.getFineAmount());
		organization.setName(bd.getOrgName());
		toReturn.setOrganization(organization);
		return toReturn;
	}

	@Override
	public BitemporalUserWithOrganizationDTO convertEntityToDTO(BitemporalUser be) 
	{
		if(be == null)
		{
			return null;
		}
		BitemporalUserWithOrganizationDTO toReturn = new BitemporalUserWithOrganizationDTO();
		toReturn.setName(be.getName());
		toReturn.setUserName(be.getUserName());
		if(be.getOrganization() != null)
		{
			BitemporalOrganization organization = be.getOrganization();
			toReturn.setEarnAmount(organization.getEarnAmount());
			toReturn.setOrgName(organization.getName());
			toReturn.setFineAmount(organization.getFineAmount());
		}
		return toReturn;
	}
}
