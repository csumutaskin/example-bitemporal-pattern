package tr.com.poc.temporaldate.core.exception; 

import static tr.com.poc.temporaldate.common.Constants.MDC_CLIENT_IP;
import static tr.com.poc.temporaldate.common.Constants.MDC_HOST_ADDRESS;
import static tr.com.poc.temporaldate.common.Constants.MDC_TRANSACTION_NO;
import static tr.com.poc.temporaldate.common.Constants.MDC_URI;
import static tr.com.poc.temporaldate.common.Constants.MDC_USERNAME;
import static tr.com.poc.temporaldate.common.ExceptionConstants.APPLICATION_ERROR_PREFIX;
import static tr.com.poc.temporaldate.common.ExceptionConstants.BUSINESS_ERROR_PREFIX;
import static tr.com.poc.temporaldate.common.ExceptionConstants.UNEXPECTED;
import static tr.com.poc.temporaldate.common.ExceptionConstants.VALIDATION_ERROR_PREFIX;
import static tr.com.poc.temporaldate.common.ExceptionConstants.USER_INPUT_NOT_VALIDATED;

import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
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

	/**
	 * An auto rest response converter in case of a BusinessException is thrown throughout the request.
	 * @param bexc thrown exception through the request
	 * @param locale locale to identify the error message languages
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleBusinessException(BusinessException bexc, Locale locale) 
	{
		String errorMessage = businessExceptionMessageSource.getMessage(bexc.getExceptionCode(), bexc.getExceptionMessageParameters(), locale);
		log.error("user's {} call has an (business) erronous response, you can enable debug mode for detailled logging. Exception detail is: {}", getThreadContextKey(MDC_URI), errorMessage);
		return prepareResponse(null, HttpStatus.BAD_REQUEST, BUSINESS_ERROR_PREFIX + bexc.getExceptionCode(), errorMessage);		
	}

	/**
	 * An auto rest response converter in case of an ApplicationException is thrown throughout the request.
	 * @param aexc thrown exception through the request
	 * @param locale locale to identify the error message languages
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleApplicationException(ApplicationException aexc, Locale locale) 
	{
		String errorMessage = applicationExceptionMessageSource.getMessage(aexc.getExceptionCode(), aexc.getExceptionMessageParameters(), locale);
		log.error("user's {} call has an (application) erronous response, you can enable debug mode for detailled logging. Exception detail is: {}", getThreadContextKey(MDC_URI), errorMessage);
		return prepareResponse(null, HttpStatus.INTERNAL_SERVER_ERROR, APPLICATION_ERROR_PREFIX + aexc.getExceptionCode(), errorMessage);
	}
	
	/**
	 * An auto rest response converter in case of an BusinessValidationException is thrown throughout the request.
	 * @param bvexc thrown exception through the request
	 * @param locale locale to identify the error message languages
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(BusinessValidationException.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleBusinessValidationException(BusinessValidationException bvexc)//, final Locale locale) 
	{		
		Deque<BusinessValidationExceptionItem> businessValidationExceptionItemList = bvexc.getBusinessValidationExceptionItemList();
		int exceptionItemSize = CollectionUtils.isEmpty(businessValidationExceptionItemList) ? 0 : businessValidationExceptionItemList.size();
		String errorCode = VALIDATION_ERROR_PREFIX + ((exceptionItemSize == 1) ? businessValidationExceptionItemList.getFirst().getExceptionItemCode() : USER_INPUT_NOT_VALIDATED);
		
		Locale locale = Locale.ENGLISH;
		boolean anyEnglishLocale = locale != null && locale.getLanguage() != null && locale.getLanguage().startsWith("en");
		String defaultMessage = anyEnglishLocale ? Constants.MESSAGE_DEAFULT_FOR_BUSINESS_EXCEPTIONS_FOR_NOT_FOUND_ERROR_CODES_EN: Constants.MESSAGE_DEAFULT_FOR_BUSINESS_EXCEPTIONS_FOR_NOT_FOUND_ERROR_CODES;
		List<ExceptionLog> errorMessageExplanations = businessValidationExceptionItemList.stream().map(bvei -> StringUtils.isBlank(bvei.getExceptionItemMessage()) ? new ExceptionLog(new StringBuilder(businessExceptionMessageSource.getMessage(bvei.getExceptionItemCode() + "|GUI", bvei.getExceptionItemMessageParameters(), defaultMessage ,locale)).append("(").append(bvei.getExceptionItemCode()).append(")").toString(), businessExceptionMessageSource.getMessage(bvei.getExceptionItemCode() + "|LOG", bvei.getExceptionItemMessageParameters(), null ,locale))	: new ExceptionLog(bvei.getExceptionItemMessage(),null)).collect(Collectors.toList());
		log.error(new StringBuilder(Constants.BUSINESS_VALIDATION_EXCEPTION_PREFIX).append(errorMessageExplanations.stream().map(el -> "[GUI:" + el.getGuiLog() + (StringUtils.isBlank(el.getServerLog()) ? "":", LOG:" + el.getServerLog()) + "]").collect(Collectors.joining(", "))).toString());
		String guiErrorMessagesAppendedString = errorMessageExplanations.stream().map(ExceptionLog::getGuiLog).collect(Collectors.joining(", "));
		return prepareResponse(null, HttpStatus.BAD_REQUEST, errorCode, guiErrorMessagesAppendedString);
	}

	/**
	 * An auto rest response converter in case of an  Unexpected Exception is thrown throughout the request.
	 * @param exc thrown exception through the request
	 * @param locale locale to identify the error message languages
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestResponse<BaseExceptionDTO>> handleUnhandledExceptions(Exception exc, Locale locale) 
	{
		HttpHeaders headers = new HttpHeaders();
		
		MediaType returnMediaType = headers.getContentType();
		if(returnMediaType == null)
		{
			headers.setContentType(MediaType.APPLICATION_JSON);
		}
		
		String errorMessage = applicationExceptionMessageSource.getMessage(UNEXPECTED, null, locale);
		log.error("user's {} call got an UNEXPECTED EXCEPTION (not converted to known exceptions), you can enable debug logs for more information, detail is: {}", getThreadContextKey(MDC_URI), ExceptionUtils.getMessage(exc));
		return prepareResponse(headers, HttpStatus.INTERNAL_SERVER_ERROR, APPLICATION_ERROR_PREFIX + UNEXPECTED, errorMessage);		
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
	
	/* Prepares an erroneous ResponseEntity Message with the given parameters and some thread context values for the request */
	private ResponseEntity<RestResponse<BaseExceptionDTO>> prepareResponse(HttpHeaders headers, HttpStatus status, String errorCode, String errorMessage)
	{
		ResponseEntity toReturn = null;
		RestResponse responseContent = new RestResponse.Builder<BaseExceptionDTO>(status.toString(), getThreadContextKey(MDC_TRANSACTION_NO))
															.withBody(null)
															.withClientIp(getThreadContextKey(MDC_CLIENT_IP))
															.withErrorCode(errorCode)
															.withErrorMessage(errorMessage)
															.withHostName(getThreadContextKey(MDC_HOST_ADDRESS))
															.withUserName(getThreadContextKey(MDC_USERNAME)).build();
		if(headers == null)
		{
			toReturn = new ResponseEntity<>(responseContent, status);
		}
		else
		{
			toReturn = new ResponseEntity<>(responseContent, headers, status);
		}
		return toReturn;
	}
	
	@AllArgsConstructor
	@Getter
	private class ExceptionLog
	{
		private String guiLog;
		private String serverLog;
	}
}
