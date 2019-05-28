package tr.com.poc.temporaldate.core.aspect;

import java.lang.reflect.Method;
import java.util.Deque;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.core.exception.BusinessValidationException;
import tr.com.poc.temporaldate.core.exception.BusinessValidationExceptionItem;
import tr.com.poc.temporaldate.core.util.request.BusinessValidationExceptionsHolder;

/**
 * Aspect to intersect all controller methods just after they run, so it is ensured that all service level methods have already run 
 * and all validation exceptions have already been collected...
 * 
 * @author umutaskin
 *
 */
@Aspect
@Order(1)
@Log4j2
public class AspectBusinessValidationExceptionChecker 
{
	@Around(Constants.EXECUTION_OF_CONTROLLER_METHODS)
	public Object checkAnyBusinessValidationException(ProceedingJoinPoint pjp) throws Throwable 
	{
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		String methodName = method.getName();
		String className = signature.getDeclaringTypeName();
		log.info("Class: " + className + ", Method: " + methodName);
		
		Object toReturn = pjp.proceed();
		
		Deque<BusinessValidationExceptionItem> businessValidationExceptionItems = BusinessValidationExceptionsHolder.getInstance().getBusinessValidationExceptionItems();
		if(!CollectionUtils.isEmpty(businessValidationExceptionItems))
		{
			throw new BusinessValidationException(businessValidationExceptionItems);
		}
		
		return toReturn;
	}
}
