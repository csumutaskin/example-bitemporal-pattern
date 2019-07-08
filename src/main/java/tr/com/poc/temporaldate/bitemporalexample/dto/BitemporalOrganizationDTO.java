package tr.com.poc.temporaldate.bitemporalexample.dto;

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
public class BitemporalOrganizationDTO extends BaseBitemporalDTO
{	
	private String name;
	private Long orgId;
	private double fineAmount;
	private double earnAmount;
}