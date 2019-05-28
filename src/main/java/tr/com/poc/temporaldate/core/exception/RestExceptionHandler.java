package tr.com.poc.temporaldate.core.exception;

import static tr.com.poc.temporaldate.common.Constants.MDC_HOST_ADDRESS;
import static tr.com.poc.temporaldate.common.Constants.MDC_TRANSACTION_NO;
import static tr.com.poc.temporaldate.common.Constants.MDC_CLIENT_IP;
import static tr.com.poc.temporaldate.common.Constants.MDC_USERNAME;
import static tr.com.poc.temporaldate.common.ExceptionConstants.UNEXPECTED;
import static tr.com.poc.temporaldate.common.ExceptionConstants.USER_INPUT_NOT_VALIDATED;
import static tr.com.poc.temporaldate.common.ExceptionConstants.APPLICATION_ERROR_PREFIX;
import static tr.com.poc.temporaldate.common.ExceptionConstants.BUSINESS_ERROR_PREFIX;
import static tr.com.poc.temporaldate.common.ExceptionConstants.VALIDATION_ERROR_PREFIX;

import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.core.util.response.RestResponse;

/**
 * Automatically handles any Propagated Exception thrown in a Rest Service Request 
 * @author umutaskin
 *
 */
@ControllerAdvice
@SuppressWarnings({ "unchecked", "rawtypes" })
@Log4j2
public class RestExceptionHandler 
{
	@Resource(name = "applicationExceptionMessageSource")
	private MessageSource applicationExceptionMessageSource;
	
	@Resource(name = "businessExceptionMessageSource")
	private MessageSource businessExceptionMessageSource;

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleBusinessException(BusinessException bexc, Locale locale) 
	{
		String errorMessage = businessExceptionMessageSource.getMessage(bexc.getExceptionCode(), bexc.getExceptionMessageParameters(), locale);
		log.error("----------------------");//TODO: Fix error messages after log format is cleared
		return new ResponseEntity<>(new RestResponse(HttpStatus.BAD_REQUEST.toString(), getThreadContextKey(MDC_TRANSACTION_NO), getThreadContextKey(MDC_HOST_ADDRESS), getThreadContextKey(MDC_CLIENT_IP), getThreadContextKey(MDC_USERNAME), BUSINESS_ERROR_PREFIX + bexc.getExceptionCode(), Stream.of(errorMessage).collect(Collectors.toList()), null), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleApplicationException(ApplicationException aexc, Locale locale) 
	{
		String errorMessage = applicationExceptionMessageSource.getMessage(aexc.getExceptionCode(), aexc.getExceptionMessageParameters(), locale);
		log.error("----------------------");
		return new ResponseEntity<>(new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), getThreadContextKey(MDC_TRANSACTION_NO), getThreadContextKey(MDC_HOST_ADDRESS), getThreadContextKey(MDC_CLIENT_IP), getThreadContextKey(MDC_USERNAME), APPLICATION_ERROR_PREFIX + aexc.getExceptionCode(), Stream.of(errorMessage).collect(Collectors.toList()), null), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(BusinessValidationException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleBusinessValidationException(BusinessValidationException bvexc, Locale locale) 
	{
		Deque<BusinessValidationExceptionItem> businessValidationExceptionItemList = bvexc.getBusinessValidationExceptionItemList();
		int exceptionItemSize = CollectionUtils.isEmpty(businessValidationExceptionItemList) ? 0 : businessValidationExceptionItemList.size();
		List<String> formattedErrorMessages = businessValidationExceptionItemList.stream().map(bvei -> new StringBuilder(StringUtils.isBlank(bvei.getExceptionItemMessage()) ? businessExceptionMessageSource.getMessage(bvei.getExceptionItemCode(), bvei.getExceptionItemMessageParameters() ,locale): bvei.getExceptionItemMessage()).append(" (").append(VALIDATION_ERROR_PREFIX).append(bvei.getExceptionItemCode()).append(")").toString()).collect(Collectors.toList());
		log.error("----------------------");
		return new ResponseEntity<>(new RestResponse(HttpStatus.BAD_REQUEST.toString(), getThreadContextKey(MDC_TRANSACTION_NO), getThreadContextKey(MDC_HOST_ADDRESS), getThreadContextKey(MDC_CLIENT_IP), getThreadContextKey(MDC_USERNAME), VALIDATION_ERROR_PREFIX + ((exceptionItemSize == 1) ? businessValidationExceptionItemList.getFirst().getExceptionItemCode() : USER_INPUT_NOT_VALIDATED) ,formattedErrorMessages, null), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleUnhandledExceptions(Exception exc, Locale locale) 
	{
		String errorMessage = applicationExceptionMessageSource.getMessage(UNEXPECTED, null, locale);
		log.error(ExceptionUtils.getMessage(exc));
		return new ResponseEntity<>(new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), getThreadContextKey(MDC_TRANSACTION_NO), getThreadContextKey(MDC_HOST_ADDRESS), getThreadContextKey(MDC_CLIENT_IP), getThreadContextKey(MDC_USERNAME), APPLICATION_ERROR_PREFIX + UNEXPECTED ,Stream.of(errorMessage).collect(Collectors.toList()), null), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	/* Replaces ThreadContext value with "N.A." if the current request URL does not get through AuditLoggingFilter to fill ThreadContext map */
	private String getThreadContextKey(String key)
	{
		String toReturn = Constants.NA;
		String threadContextValue = ThreadContext.get(key);
		if(StringUtils.isNotBlank(threadContextValue))
		{
			toReturn = threadContextValue;
		}
		return toReturn;
	}
}
