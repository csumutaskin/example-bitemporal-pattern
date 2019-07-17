package tr.com.poc.temporaldate.bitemporalexample.dto.bitemporaluser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalDTO;

@SuppressWarnings("serial")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BitemporalUserWithoutOrganizationDTO extends BaseBitemporalDTO 
{
	private String userName;
	private String name;
}
