package tr.com.poc.temporaldate.onlyauditdatesexample.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.onlyauditdatesexample.service.AuditedOrganizationService;
import tr.com.poc.temporaldate.temporalexample.dto.TemporalOrganizationDTO;

/**
 * Sample organization controller having version data
 * 
 * @author umut
 */
@RestController
@Log4j2
@RequestMapping(value = "/versioned-organization")
public class AuditedOrganizationController 
{
	@Autowired
	private AuditedOrganizationService versionedOrganizationService;
	
	@GetMapping(value = "/getAll" , produces= {"application/json"})
	public ResponseEntity<List<TemporalOrganizationDTO>> getOrganizationList()
	{		
		return new ResponseEntity<>(versionedOrganizationService.getAllOrganizations(), HttpStatus.OK);
	}
	
	@PutMapping(value = "/update/{id}" , consumes = {"application/json"}, produces= {"application/json"})
	public ResponseEntity<Boolean> updateOrganization(@PathVariable BigDecimal id, @RequestBody TemporalOrganizationDTO toUpdate)
	{		
		return new ResponseEntity<>(versionedOrganizationService.updateVersionedOrganization(id, toUpdate), HttpStatus.OK);
	}
	
	@PostMapping(value = "/save" , consumes = {"application/json"}, produces= {"application/json"})
	public ResponseEntity<Boolean> saveOrganization(@RequestBody TemporalOrganizationDTO toSave)
	{	
		BigDecimal organizationId = versionedOrganizationService.saveVersionedOrganization(toSave);
		log.debug("Organization created with id: {}", organizationId);
		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<Boolean> deleteOrganization(@PathVariable BigDecimal id)
	{		
		return new ResponseEntity<>(versionedOrganizationService.deleteVersionedOrganization(id), HttpStatus.OK);
	}
}
