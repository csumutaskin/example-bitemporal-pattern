package tr.com.poc.temporaldate.core.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Unexpected Exception thrown throughout the life cycle of the software
 *  
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter 
@Setter 
@ToString
public class ApplicationException extends BaseException 
{
	private Exception causeException;	
	
	public ApplicationException(String exceptionCode, String... exceptionMessageParameters)
	{
		super(exceptionCode);
		this.setExceptionMessageParameters(exceptionMessageParameters);
	}
	
	public ApplicationException(String exceptionCode, Exception causeException, String... exceptionMessageParameters)
	{
		super(exceptionCode);
		this.causeException = causeException;
		this.setExceptionMessageParameters(exceptionMessageParameters);
	}
}
