package tr.com.poc.temporaldate.core.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;

@Aspect
@Order(1)
@Log4j2
public class AspectBusinessValidationExceptionChecker 
{
	@Around(Constants.EXECUTION_OF_CONTROLLER_METHODS)
	public Object checkException(ProceedingJoinPoint pjp) throws Throwable 
	{
		MethodSignature signature = (MethodSignature) pjp.getSignature();
		Method method = signature.getMethod();
		String methodName = method.getName();
		String className = signature.getDeclaringTypeName();
		log.info("Class: " + className + ", Method: " + methodName);
		
		
		
		
		return pjp.proceed();		
	}
}
