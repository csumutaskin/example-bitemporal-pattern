package tr.com.poc.temporaldate.core.model.bitemporal;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * Base DTO for Bitemporal entities
 * "Record Dates" hold perspective time details whereas "effective Dates" mean in which interval those tuples are active.
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
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH")
	private LocalDateTime perspectiveDateStart;
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private LocalDateTime perspectiveDateEnd;
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private LocalDateTime effectiveDateStart;
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
	private LocalDateTime effectiveDateEnd;
}
