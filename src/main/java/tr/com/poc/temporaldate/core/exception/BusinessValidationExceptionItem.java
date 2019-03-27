package tr.com.poc.temporaldate.core.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
