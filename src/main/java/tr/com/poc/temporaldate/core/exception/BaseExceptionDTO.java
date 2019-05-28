package tr.com.poc.temporaldate.core.exception;

import tr.com.poc.temporaldate.core.model.BaseDTO;

/**
 * A common DTO model to be used in response in case of an error/exception is thrown in a request thread.
 * If there is an error in processing a request than no need to give the rest response body in detail,
 * error codes and messages are important in such a case than the response body 
 *  
 * @author umutaskin
 *
 */
@SuppressWarnings("serial")
public class BaseExceptionDTO implements BaseDTO 
{}
