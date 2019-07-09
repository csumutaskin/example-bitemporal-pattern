package tr.com.poc.temporaldate.bitemporalexample.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.service.BitemporalOrganizationService;
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
public class BitemporalOrganizationController 
{
	@Autowired
	private BitemporalOrganizationService bitemporalOrganizationService;
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalOrganizationReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link RestResponse of BitemporalOrganizationDTO}
	 */
	@PostMapping(value = "/getAll" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BitemporalOrganizationDTO>> getOrganizationList(@RequestBody BitemporalOrganizationReadRequestDTO toRead)
	{		
		List<BitemporalOrganizationDTO> allOrganizations = bitemporalOrganizationService.getAllOrganizations(toRead);
		RestResponse<BitemporalOrganizationDTO> build = new RestResponse.Builder<BitemporalOrganizationDTO>(HttpStatus.OK.toString()).withBodyList(allOrganizations).build();
		log.debug("Organization list retrieved using /bitemporal-organization/getAll rest");
		return new ResponseEntity<>(build, HttpStatus.OK);		
	}
	
	/**
	 * Saves or Updates the given {@link BitemporalOrganizationDTO} object
	 * @param id if null, persist operation is done, if non-null update operation is done
	 * @param toSaveOrUpdate object to be persisted or updated
	 * @return {@link BitemporalOrganizationDTO} Saved or Updated object details
	 */
	@PostMapping(value = "/saveOrUpdate/{id}" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BitemporalOrganizationDTO>> saveOrUpdateOrganization(@ApiParam(required=false) @PathVariable(required=false) Optional<String> id, @RequestBody BitemporalOrganizationDTO toSaveOrUpdate)
	{		
		BitemporalOrganizationDTO toReturn = null;
		if(!id.isPresent() || Constants.UNDEFINED_STR.equalsIgnoreCase(id.get()))
		{			
			toReturn = bitemporalOrganizationService.saveOrMergeOrganization(null, toSaveOrUpdate);
			log.debug("Organization created with @pid: {}", toReturn.getOrgId());
		}	
		else
		{
			BigDecimal bd = new BigDecimal(id.get());
			toReturn = bitemporalOrganizationService.saveOrMergeOrganization(bd, toSaveOrUpdate);
			log.debug("Organization created with @pid: {}", toReturn.getOrgId());
		}
		RestResponse<BitemporalOrganizationDTO> response = new RestResponse.Builder<BitemporalOrganizationDTO>(HttpStatus.OK.toString()).withBody(toReturn).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalOrganizationReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link RestResponse of BitemporalOrganizationDTO}
	 */
	@DeleteMapping(value = "/deleteEntities" , consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BooleanDTO>> deleteOrganizations(@RequestBody BitemporalOrganizationReadRequestDTO toDelete)
	{		
		bitemporalOrganizationService.removeOrganizations(toDelete);
		RestResponse<BooleanDTO> response = new RestResponse.Builder<BooleanDTO>(HttpStatus.OK.toString()).withBody(new BooleanDTO(Boolean.TRUE)).build();
		log.debug("Organization entities deleted using /bitemporal-organization/deleteEntities rest");
		return new ResponseEntity<>(response, HttpStatus.OK);		
	}
}
