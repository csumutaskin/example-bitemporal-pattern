package tr.com.poc.temporaldate.bitemporalexample.dto.bitemporalorganization;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;

/**
 * A Sample DTO object for Rest Operations of {@link BitemporalOrganization}
 * @author umutaskin
 *
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor 
@Getter 
@Setter 
@ToString
public class BitemporalOrganizationSaveOrUpdateRequestDTO extends BaseBitemporalDTO
{	
	private String name;	
	private double fineAmount;
	private double earnAmount;
}