package tr.com.poc.temporaldate.bitemporalexample.dto.common;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tr.com.poc.temporaldate.bitemporalexample.model.BitemporalOrganization;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * A Sample DTO object for Read All /getAll Operations of {@link BitemporalOrganization}
 * @author umutaskin
 */
@SuppressWarnings("serial")
@AllArgsConstructor
@NoArgsConstructor 
@Getter 
@Setter 
@ToString
public class BitemporalReadRequestDTO implements BaseDTO
{	
	private Long pid;
	private LocalDateTime atEffectiveTime;
	private LocalDateTime atPerspectiveTime;
}
