package tr.com.poc.temporaldate.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.core.model.BaseDTO;

@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor 
@Getter 
@Setter 
@ToString
public class OrganizationDTO implements BaseDTO
{
	private String name;
	private String shortName;
	private double fineAmount;
	private double earnAmount;
}
