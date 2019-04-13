package tr.com.poc.temporaldate.temporalexample.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.model.BaseTemporalDTO;

@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor 
@Getter 
@Setter 
@ToString
public class OrganizationDTO extends BaseTemporalDTO
{
	private String name;
	private String shortName;
	private double fineAmount;
	private double earnAmount;
}
