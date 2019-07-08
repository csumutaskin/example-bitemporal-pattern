package tr.com.poc.temporaldate.bitemporalexample.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.service.BitemporalOrganizationService;
import tr.com.poc.temporaldate.core.util.logging.RestLoggable;
import tr.com.poc.temporaldate.core.util.response.RestResponse;

/**
 * Sample organization controller having bi-temporal data
 * 
 * @author umut
 */
@RestController
@Log4j2
@RequestMapping(value = "/bitemporal-organization")
@RestLoggable
public class BitemporalOrganizationController 
{
	@Autowired
	private BitemporalOrganizationService bitemporalOrganizationService;
	
	@GetMapping(value = "/getAll" , produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BitemporalOrganizationDTO>> getOrganizationList()
	{		
		List<BitemporalOrganizationDTO> allOrganizations = bitemporalOrganizationService.getAllOrganizations(LocalDateTime.now(), null);
		RestResponse<BitemporalOrganizationDTO> build = new RestResponse.Builder<BitemporalOrganizationDTO>(HttpStatus.OK.toString()).withBodyList(allOrganizations).build();
		return new ResponseEntity<>(build, HttpStatus.OK);		
	}
	
	//TODO: Gereksiz sil....
	@GetMapping(value = "/getAll2" , produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BitemporalOrganizationDTO>> getOrganizationItem()
	{		
		BitemporalOrganizationDTO a = new BitemporalOrganizationDTO("aboooTek1", 15l, 3d, 5d);
		RestResponse<BitemporalOrganizationDTO> build = new RestResponse.Builder<BitemporalOrganizationDTO>(HttpStatus.OK.toString()).withBody(a).build();
		return new ResponseEntity<>(build, HttpStatus.OK);		
	}
	
	/**	 
	 * @param id
	 * @param toUpdate
	 * @return
	 */
	@PutMapping(value = "/update/{id}" , consumes = {MediaType.APPLICATION_JSON_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<Boolean> updateOrganization(@PathVariable BigDecimal id, @RequestBody BitemporalOrganizationDTO toUpdate)
	{
		return new ResponseEntity<>(bitemporalOrganizationService.updateOrganization(id, toUpdate), HttpStatus.OK);
	}
		
	@PostMapping(value = "/saveOrMerge/{id}" , consumes = {MediaType.APPLICATION_JSON_VALUE}, produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BitemporalOrganizationDTO>> saveOrMergeOrganization(@ApiParam(required=false) @PathVariable(required=false) Optional<String> id, @RequestBody BitemporalOrganizationDTO toSaveOrUpdate)
	{		
		BitemporalOrganizationDTO toReturn = null;
		if(!id.isPresent() || "undefined".equalsIgnoreCase(id.get()))
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
	
	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<Boolean> deleteOrganization(@PathVariable BigDecimal id)
	{		
		return new ResponseEntity<>(bitemporalOrganizationService.deleteOrganization(id), HttpStatus.OK);
	}
}
