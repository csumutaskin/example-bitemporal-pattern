package tr.com.poc.temporaldate.core.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Unexpected Exception thrown throughout the life cycle of the software
 *  
 * @author umut
 */
@SuppressWarnings("serial")
@AllArgsConstructor 
@NoArgsConstructor
@Getter 
@Setter 
@ToString 
@Builder
public class ApplicationException extends BaseException 
{
	private Long exceptionCode;
	private String exceptionMessage;
	//private String exceptionLogMessage;
	private Exception causeException;
}
