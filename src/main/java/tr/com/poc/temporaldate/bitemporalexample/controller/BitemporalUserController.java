package tr.com.poc.temporaldate.bitemporalexample.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateResponseDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser.BitemporalUserWithoutOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.common.BitemporalReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalUser;
import tr.com.poc.temporaldate.bitemporalexample.service.BitemporalUserService;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.core.util.response.RestResponse;

/**
 * A sample rest controller for Bitemporal {@link BitemporalUser} entity 
 * @author umutaskin
 *
 */
@RestController
@Log4j2
@RequestMapping(value = "/bitemporal-user")
@ResponseBody
public class BitemporalUserController 
{
	@Autowired
	private BitemporalUserService userService;
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link RestResponse of BitemporalOrganizationDTO}
	 */
	@PostMapping(value = "/getAllUsersWithoutOrganizationInfo" , produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public RestResponse<BitemporalUserWithoutOrganizationDTO> getUserWithoutOrganizationList(@RequestBody BitemporalReadRequestDTO toRead)
	{			
		return new RestResponse.Builder<BitemporalUserWithoutOrganizationDTO>(HttpStatus.OK.toString()).withBodyList(userService.getAllUsersWithoutOrganization(toRead)).build();				
	}
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link RestResponse of BitemporalOrganizationDTO}
	 */
	@PostMapping(value = "/getAllUsersWithOrganizationInfo" , produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public RestResponse<BitemporalUserWithOrganizationDTO> getUserWithOrganizationList(@RequestBody BitemporalReadRequestDTO toRead)
	{			
		return new RestResponse.Builder<BitemporalUserWithOrganizationDTO>(HttpStatus.OK.toString()).withBodyList(userService.getAllUsersWithOrganization(toRead)).build();				
	}
	
	/**
	 * Saves or Updates the given {@link BitemporalOrganizationSaveOrUpdateResponseDTO} object
	 * @param id if null, persist operation is done, if non-null update operation is done
	 * @param toSaveOrUpdate object to be persisted or updated
	 * @return {@link BitemporalOrganizationSaveOrUpdateResponseDTO} Saved or Updated object details
	 */
	@PostMapping(value = "/save" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public RestResponse<BitemporalUserWithOrganizationDTO> saveOrUpdateUserWithOrganization(@ApiParam(required=false) @PathVariable(required=false) Optional<String> userId, @RequestBody BitemporalUserWithOrganizationDTO toSaveOrUpdate)
	{		
		BitemporalUserWithOrganizationDTO toReturn = null;
		if(!userId.isPresent() || Constants.UNDEFINED_STR.equalsIgnoreCase(userId.get()))
		{			
			toReturn = userService.saveOrMergeUserWithOrganization(null, toSaveOrUpdate);
			log.debug("User created with username: {}", toReturn.getUserName());
		}	
		else
		{
			BigDecimal bd = new BigDecimal(userId.get());
			toReturn = userService.saveOrMergeUserWithOrganization(bd, toSaveOrUpdate);
			log.debug("User created with username: {}", toReturn.getUserName());
		}		
		return new RestResponse.Builder<BitemporalUserWithOrganizationDTO>(HttpStatus.OK.toString()).withBody(toReturn).build();	
	}
}
