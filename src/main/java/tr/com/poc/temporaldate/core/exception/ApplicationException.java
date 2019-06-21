package tr.com.poc.temporaldate.core.exception;

import lombok.Getter;
import lombok.ToString;

/**
 * Unexpected Exception thrown throughout the life cycle of the software
 *  
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter 
@ToString
public class ApplicationException extends BaseException 
{
	private final Exception causeException;	
	
	public ApplicationException(String exceptionCode, String... exceptionMessageParameters)
	{
		super(exceptionCode, null, null, exceptionMessageParameters);		
		this.causeException = null;
	}
	
	public ApplicationException(String exceptionCode, Exception causeException, String... exceptionMessageParameters)
	{
		super(exceptionCode, null, null, exceptionMessageParameters);
		this.causeException = causeException;		
	}
}
