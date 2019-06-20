package tr.com.poc.temporaldate.core.exception;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Part of a particular validation exception that contribute to the "user warning message" in a request-response life cycle that is triggered by an end user@SuppressWarnings("serial")

 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Setter
@Getter
@ToString
public class BusinessValidationExceptionItem implements Serializable
{
	private String exceptionItemCode;
	private String exceptionItemMessage;
	private String[] exceptionItemMessageParameters;
	
	public BusinessValidationExceptionItem(String exceptionItemCode, String... exceptionItemMessageParameters)
	{
		this.exceptionItemCode = exceptionItemCode;		
		this.exceptionItemMessageParameters = exceptionItemMessageParameters;
	}	
}
