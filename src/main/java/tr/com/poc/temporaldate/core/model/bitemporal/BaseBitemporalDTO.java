package tr.com.poc.temporaldate.core.model.bitemporal;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * Base DTO for Bitemporal entities
 * "Record Dates" hold observer time details whereas "effective Dates" mean in which interval those tuples are active.
 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
public class BaseBitemporalDTO implements BaseDTO
{
	private LocalDateTime effectiveDateStart;
	private LocalDateTime effectiveDateEnd;
}
