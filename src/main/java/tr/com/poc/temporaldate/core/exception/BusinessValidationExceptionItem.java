package tr.com.poc.temporaldate.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Part of a particular validation exception that contribute to the "user warning message" in a request-response life cycle that is triggered by an end user
 * 
 * @author umutaskin
 */
@AllArgsConstructor 
@NoArgsConstructor
@Setter
@Getter
@ToString
public class BusinessValidationExceptionItem 
{
	private Long exceptionItemCode;
	private String exceptionItemMessage;
	//private String exceptionItemLogMessage;
}
