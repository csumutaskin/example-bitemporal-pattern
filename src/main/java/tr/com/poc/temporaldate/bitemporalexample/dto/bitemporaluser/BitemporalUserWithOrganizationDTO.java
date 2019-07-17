package tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import tr.com.poc.temporaldate.core.annotations.validation.NotNull;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;

@SuppressWarnings("serial")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BitemporalUserWithOrganizationDTO extends BaseBitemporalDTO 
{
	private String userName;
	private String name;
	private String orgName;
	private double fineAmount;
	private double earnAmount;
}
