package tr.com.poc.temporaldate.core.exception;

import static tr.com.poc.temporaldate.common.Constants.MDC_HOST_ADDRESS;
import static tr.com.poc.temporaldate.common.Constants.MDC_TRANSACTION_NO;
import static tr.com.poc.temporaldate.common.Constants.MDC_USERNAME;
import static tr.com.poc.temporaldate.common.ExceptionConstants.UNEXPECTED_EXCEPTION;

import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

	@SuppressWarnings("unchecked")
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleBusinessException(BusinessException bexc, Locale locale) 
	{
		String errorMessage = messageSource.getMessage(bexc.getExceptionCode(), bexc.getExceptionMessageParameters(), locale);
		return new ResponseEntity<>(new RestResponse(HttpStatus.BAD_REQUEST.toString(), ThreadContext.get(MDC_TRANSACTION_NO), ThreadContext.get(MDC_HOST_ADDRESS), ThreadContext.get(MDC_USERNAME),null, null), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleApplicationException(ApplicationException aexc, Locale locale) 
	{
		String errorMessage = messageSource.getMessage(aexc.getExceptionCode(), aexc.getExceptionMessageParameters(), locale);
	//	return new ResponseEntity<>(new RestResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		return null;
	}
	
	@ExceptionHandler(BusinessValidationException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleApplicationException(BusinessValidationException bvexc, Locale locale) 
	{
		Deque<BusinessValidationExceptionItem> businessValidationExceptionItemList = bvexc.getBusinessValidationExceptionItemList();
	//	List<String> errorMessages = businessValidationExceptionItemList.stream().map(objectError -> messageSource.getMessage(objectError, locale)).collect(Collectors.toList());
	//	return new ResponseEntity<>(new RestResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		return null;
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleExceptions(Exception exc, Locale locale) 
	{
		String errorMessage = messageSource.getMessage(UNEXPECTED_EXCEPTION, null, locale);
	//	ex.printStackTrace();
	//	return new ResponseEntity<>(new RestResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
		return null;
	}
}
