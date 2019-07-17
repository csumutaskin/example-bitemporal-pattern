package tr.com.poc.temporaldate.bitemporalexample.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization.BitemporalOrganizationSaveOrUpdateResponseDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.common.BitemporalReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.service.BitemporalOrganizationService;
import tr.com.poc.temporaldate.core.util.logging.RestLoggable;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.core.model.BooleanDTO;
import tr.com.poc.temporaldate.core.util.response.RestResponse;

/**
 * A Bitemporal Organization Rest Collection Example
 * 
 * @author umut
 */
@RestController
@Log4j2
@RequestMapping(value = "/bitemporal-organization")
@RestLoggable
@ResponseBody
public class BitemporalOrganizationController
{
	@Autowired
	private BitemporalOrganizationService bitemporalOrganizationService;
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link RestResponse of BitemporalOrganizationDTO}
	 */
	@PostMapping(value = "/getAll" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public RestResponse<BitemporalOrganizationSaveOrUpdateResponseDTO> getOrganizationList(@RequestBody BitemporalReadRequestDTO toRead)
	{		
		List<BitemporalOrganizationSaveOrUpdateResponseDTO> allOrganizations = bitemporalOrganizationService.getAllOrganizations(toRead);
		log.debug("Organization list retrieved using /bitemporal-organization/getAll rest");
		return new RestResponse.Builder<BitemporalOrganizationSaveOrUpdateResponseDTO>(HttpStatus.OK.toString()).withBodyList(allOrganizations).build();				
	}
	
	/**
	 * Saves or Updates the given {@link BitemporalOrganizationSaveOrUpdateResponseDTO} object
	 * @param id if null, persist operation is done, if non-null update operation is done
	 * @param toSaveOrUpdate object to be persisted or updated
	 * @return {@link BitemporalOrganizationSaveOrUpdateResponseDTO} Saved or Updated object details
	 */
	@PostMapping(value = "/saveOrUpdate/{orgId}" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public RestResponse<BitemporalOrganizationSaveOrUpdateResponseDTO> saveOrUpdateOrganization(@ApiParam(required=false) @PathVariable(required=false) Optional<String> orgId, @RequestBody BitemporalOrganizationSaveOrUpdateRequestDTO toSaveOrUpdate)
	{		
		BitemporalOrganizationSaveOrUpdateResponseDTO toReturn = null;
		if(!orgId.isPresent() || Constants.UNDEFINED_STR.equalsIgnoreCase(orgId.get()))
		{
			toReturn = bitemporalOrganizationService.saveOrMergeOrganization(null, toSaveOrUpdate);
			log.debug("Organization created with @pid: {}", toReturn.getOrgId());
		}	
		else
		{
			BigDecimal bd = new BigDecimal(orgId.get());
			toReturn = bitemporalOrganizationService.saveOrMergeOrganization(bd, toSaveOrUpdate);
			log.debug("Organization created with @pid: {}", toReturn.getOrgId());
		}
		return new RestResponse.Builder<BitemporalOrganizationSaveOrUpdateResponseDTO>(HttpStatus.OK.toString()).withBody(toReturn).build();		
	}
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link RestResponse of BitemporalOrganizationDTO}
	 */
	@DeleteMapping(value = "/deleteEntities" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public RestResponse<BooleanDTO> deleteOrganizations(@RequestBody BitemporalReadRequestDTO toDelete)
	{		
		bitemporalOrganizationService.removeOrganizations(toDelete);
		log.debug("Organization entities deleted using /bitemporal-organization/deleteEntities rest");
		return new RestResponse.Builder<BooleanDTO>(HttpStatus.OK.toString()).withBody(new BooleanDTO(Boolean.TRUE)).build();				
	}
}
