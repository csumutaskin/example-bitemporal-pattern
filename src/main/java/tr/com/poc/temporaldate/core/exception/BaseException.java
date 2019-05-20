package tr.com.poc.temporaldate.core.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Marker class on Exceptions thrown throughout the software
 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter 
@Setter 
@ToString 
public class BaseException extends RuntimeException
{
	private String exceptionCode; //Exception Code is hold here
	private String exceptionMessage; // Exception Message Template (without replaceable parameters are replaced) is hold here
	private String logMessage;	// Application Text Log Message Template (without replaceable parameters are replaced) is hold here
	private String[] exceptionMessageParameters; //Message Bundle replaceable parameters are hold here to create an error message dynamically
	
	public BaseException(String exceptionCode)
	{
		this.exceptionCode = exceptionCode;
	}	
}
