package tr.com.poc.temporaldate.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Simple DTO to indicate whether the rest operation ended succuesfully or not
 * 
 * @author umutaskin
 *
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class BooleanDTO implements BaseDTO 
{
	private Boolean completed;
}
