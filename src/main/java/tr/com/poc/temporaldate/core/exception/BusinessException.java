package tr.com.poc.temporaldate.core.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Validation exceptions that are thrown and can be fixed if customer input changes correctly
 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter 
@Setter
@ToString
public class BusinessException extends BaseException 
{
	private Exception causeException;
	
	public BusinessException(String exceptionCode, String... exceptionMessageParameters)
	{
		super(exceptionCode);
		this.setExceptionMessageParameters(exceptionMessageParameters);
	}
	
	public BusinessException(String exceptionCode, Exception causeException, String... exceptionMessageParameters)
	{
		super(exceptionCode);
		this.causeException = causeException;
		this.setExceptionMessageParameters(exceptionMessageParameters);
	}
}
