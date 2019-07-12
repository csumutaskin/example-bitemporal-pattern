package tr.com.poc.temporaldate.bitemporalexample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationDTO;
import tr.com.poc.temporaldate.bitemporalexample.dto.BitemporalOrganizationReadRequestDTO;
import tr.com.poc.temporaldate.bitemporalexample.model.User;
import tr.com.poc.temporaldate.bitemporalexample.service.UserService;
import tr.com.poc.temporaldate.core.model.BooleanDTO;
import tr.com.poc.temporaldate.core.util.response.RestResponse;

@RestController
@Log4j2
@RequestMapping(value = "/user")
public class UserController 
{
	@Autowired
	private UserService userService;
	
	/**
	 * Retrieves all organization data with the given parameter set in {@link BitemporalOrganizationReadRequestDTO}
	 * @param toRead input parameters for read criteria
	 * @return List of {@link RestResponse of BitemporalOrganizationDTO}
	 */
	@SuppressWarnings("unused")
	@PostMapping(value = "/getAll" , produces= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BooleanDTO>> getOrganizationList()
	{		
		User allOrganizations = userService.getAllOrganizations();
		RestResponse<BooleanDTO> build = new RestResponse.Builder<BooleanDTO>(HttpStatus.OK.toString()).withBody(new BooleanDTO(Boolean.TRUE)).build();
		log.debug("Organization list retrieved using /bitemporal-organization/getAll rest");
		return new ResponseEntity<>(build, HttpStatus.OK);		
	}
	
	/**
	 * Saves or Updates the given {@link BitemporalOrganizationDTO} object
	 * @param id if null, persist operation is done, if non-null update operation is done
	 * @param toSaveOrUpdate object to be persisted or updated
	 * @return {@link BitemporalOrganizationDTO} Saved or Updated object details
	 */
	@GetMapping(value = "/save" , produces= {MediaType.APPLICATION_JSON_VALUE,MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<RestResponse<BooleanDTO>> saveOrUpdateUserWithOrganization()
	{		
		userService.saveUserWithOrganization();
		RestResponse<BooleanDTO> response = new RestResponse.Builder<BooleanDTO>(HttpStatus.OK.toString()).withBody(new BooleanDTO(Boolean.TRUE)).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
