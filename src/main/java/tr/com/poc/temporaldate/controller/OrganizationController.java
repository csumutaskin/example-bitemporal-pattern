package tr.com.poc.temporaldate.controller;

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
import tr.com.poc.temporaldate.dto.OrganizationDTO;
import tr.com.poc.temporaldate.service.OrganizationService;

@RestController
@Log4j2
@RequestMapping(value = "/organization")
public class OrganizationController 
{
	@Autowired
	private OrganizationService organizationService;
	
	@GetMapping(value = "/getAll" , produces= {"application/json", "application/xml"})
	public ResponseEntity<List<OrganizationDTO>> getOrganizationList()
	{		
		return new ResponseEntity<>(organizationService.getAllOrganizations(), HttpStatus.OK);
	}
	
	@PutMapping(value = "/update/{id}" , consumes = {"application/json", "application/xml"}, produces= {"application/json", "application/xml"})
	public ResponseEntity<Boolean> updateOrganization(@PathVariable BigDecimal id, @RequestBody OrganizationDTO toUpdate)
	{		
		return new ResponseEntity<>(organizationService.updateOrganization(id, toUpdate), HttpStatus.OK);
	}
	
	@PostMapping(value = "/save" , consumes = {"application/json", "application/xml"}, produces= {"application/json", "application/xml"})
	public ResponseEntity<Boolean> saveOrganization(@RequestBody OrganizationDTO toSave)
	{		
		BigDecimal organizationId = organizationService.saveOrganization(toSave);
		log.debug("Organization created with id: {}", organizationId);
		return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
	}
	
	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<Boolean> deleteOrganization(@PathVariable BigDecimal id)
	{		
		return new ResponseEntity<>(organizationService.deleteOrganization(id), HttpStatus.OK);
	}
}