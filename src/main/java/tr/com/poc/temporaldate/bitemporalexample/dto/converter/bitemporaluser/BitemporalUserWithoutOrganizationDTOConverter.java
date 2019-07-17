package tr.com.poc.temporaldate.bitemporalexample.dto.converter.bitemporaluser;

import org.springframework.stereotype.Component;

import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithoutOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalUser;
import tr.com.poc.temporaldate.core.converter.BaseBitemporalConverter;
import tr.com.poc.temporaldate.core.util.Trim;

/**
 * Converter between {@link BitemporalUser} and {@link BitemporalUserWithoutOrganizationDTO} objects
 * @author umutaskin
 */
@Component
public class BitemporalUserWithoutOrganizationDTOConverter extends BaseBitemporalConverter<BitemporalUser, BitemporalUserWithoutOrganizationDTO> 
{

	@Override
	public Trim setEffectiveBeginDateTrimType() 
	{
		return null;
	}

	@Override
	public BitemporalUser convertDTOToEntity(BitemporalUserWithoutOrganizationDTO bd) 
	{
		if(bd == null)
		{
			return null;
		}
		BitemporalUser toReturn = new BitemporalUser();
		toReturn.setUserName(bd.getUserName());
		toReturn.setName(bd.getName());
		return toReturn;
	}

	@Override
	public BitemporalUserWithoutOrganizationDTO convertEntityToDTO(BitemporalUser be) 
	{
		if(be == null)
		{
			return null;
		}
		return new BitemporalUserWithoutOrganizationDTO(be.getUserName(), be.getName());
	}
}
