package tr.com.poc.temporaldate.core.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.core.model.NoBodyDTO;
import tr.com.poc.temporaldate.core.util.response.RestResponse;

/**
 * In case a well known URL is mapped to this controller, on that profile, the developer does not want to expose that URL to the outside world.
 * e.g. A swagger URL can be exposed on development profile, whereas on a production profile, the same URL maps here. 
 * 
 * @author umutaskin
 *
 */
@RestController
@Profile(value= {Constants.PROFILE_PROD, Constants.PROFILE_PREPROD})
@Log4j2
public class NotFoundController 
{
	@Value("${spring.profiles.active:NoProfileChosen}")
	private String activeProfile;
	
	@GetMapping(value = {"/swagger-ui.html"}, produces= {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<RestResponse<NoBodyDTO>> promptResourceNotFound()
	{
		log.info("Current profile is: {}. SWAGGER is not enabled on this profile.", activeProfile);
		RestResponse<NoBodyDTO> toReturn = new RestResponse<NoBodyDTO>(HttpStatus.OK.toString(), null, null , null, null, null, null, null);
		return new ResponseEntity<>(toReturn, HttpStatus.OK);
	}
}
