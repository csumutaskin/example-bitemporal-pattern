package tr.com.poc.temporaldate.core.exception;

import static tr.com.poc.temporaldate.common.ExceptionConstants.UNEXPECTED_EXCEPTION;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import tr.com.poc.temporaldate.core.util.RestResponse;

/**
 * Automatically handles any Propagated Exception thrown in a Rest Service Request 
 * @author umutaskin
 *
 */
@ControllerAdvice
public class RestExceptionHandler 
{
	private final MessageSource messageSource;

	@Autowired
	public RestExceptionHandler(MessageSource messageSource) 
	{
		this.messageSource = messageSource;
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleBusinessException(RestException ex, Locale locale) 
	{
		String errorMessage = messageSource.getMessage(ex.getMessage(), ex.getArgs(), locale);
		return new ResponseEntity<>(new RestResponse(errorMessage), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<RestMessage> handleApplicationException(MethodArgumentNotValidException ex, Locale locale) 
	{
		BindingResult result = ex.getBindingResult();
		List<String> errorMessages = result.getAllErrors().stream()
				.map(objectError -> messageSource.getMessage(objectError, locale)).collect(Collectors.toList());
		return new ResponseEntity<>(new RestMessage(errorMessages), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestMessage> handleExceptions(Exception ex, Locale locale) 
	{
		String errorMessage = messageSource.getMessage(UNEXPECTED_ERROR, null, locale);
		ex.printStackTrace();
		return new ResponseEntity<>(new RestMessage(errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
