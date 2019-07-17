package tr.com.poc.temporaldate.core.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.CollectionUtils;

import tr.com.poc.temporaldate.core.annotations.validation.Valid;
import tr.com.poc.temporaldate.core.configuration.ApplicationContextProvider;
import tr.com.poc.temporaldate.core.exception.BusinessValidationException;
import tr.com.poc.temporaldate.core.exception.BusinessValidationExceptionItem;
import tr.com.poc.temporaldate.core.model.BaseDTO;
import tr.com.poc.temporaldate.core.util.request.BusinessValidationExceptionsHolder;
import tr.com.poc.temporaldate.core.validation.BussinesValidator;
import tr.com.poc.temporaldate.core.validation.ValidAspectInformer;

/**
 * Aspect BussinesValidator class, Sets Request Path for each request
 * 
 * @author umutaskin
 */
@Aspect
@Order(13)
public class AspectValidator
{

	@Autowired
	private Validator validator;

	public void setVaidator(Validator validator)
	{
		this.validator = validator;
	}

	@SuppressWarnings("unchecked")
	@Before("execution(* *(..,@tr.com.poc.temporaldate.core.annotations.validation.Valid (*),..))")
	public void valid(JoinPoint jp) throws NoSuchMethodException
	{
		Set<ConstraintViolation<?>> violations = new HashSet<>();
		Method interfaceMethod = ((MethodSignature) jp.getSignature()).getMethod();
		Class<?> targetClass = jp.getTarget().getClass();
		Method implementationMethod = targetClass.getMethod(interfaceMethod.getName(), interfaceMethod.getParameterTypes());

		String path = implementationMethod.getDeclaringClass().getName() + "." + implementationMethod.getName();
		ValidAspectInformer.getInstance().setPath(path);

		Annotation[][] annotationParameters = implementationMethod.getParameterAnnotations();
		Boolean throwManally = false;
		try
		{
			for (int i = 0; i < annotationParameters.length; i++)
			{
				Annotation[] annotations = annotationParameters[i];
				for (Annotation annotation : annotations)
				{
					if (annotation.annotationType().equals(Valid.class))
					{
						Valid valid = (Valid) annotation;
						Object arg = jp.getArgs()[i];
						violations.addAll(this.validator.validate(arg, valid.groups()));
						if (valid.throwManually())
						{
							throwManally = true;
						}

						if (!CollectionUtils.isEmpty(Arrays.asList(valid.validator())))
						{
							for (Class<? extends BussinesValidator> aClass : valid.validator())
							{
								BussinesValidator bussinesValidator = (BussinesValidator) ApplicationContextProvider.getBeanFromApplicationContext(aClass);
								bussinesValidator.valid((BaseDTO) arg);
							}
						}

					}
				}
			}
			// Throw an exception if ConstraintViolations are found
			if (!violations.isEmpty())
			{
				ValidAspectInformer.getInstance().remove();
				throw new ConstraintViolationException(violations);
			}
			else
			{
				ValidAspectInformer.getInstance().remove();
			}
		}
		catch (ConstraintViolationException ex)
		{
			ex.getConstraintViolations().stream().forEach(this::enrichBussinesValidationExceptionFromConstraintViolation);

		}
		Deque<BusinessValidationExceptionItem> businessValidationExceptionItems = BusinessValidationExceptionsHolder.getInstance().getBusinessValidationExceptionItems();
		if (!throwManally && !CollectionUtils.isEmpty(businessValidationExceptionItems))
		{
			throw new BusinessValidationException(businessValidationExceptionItems);
		}

	}

	private void enrichBussinesValidationExceptionFromConstraintViolation(ConstraintViolation<?> violation)
	{
		Map<String, Object> attributes = violation.getConstraintDescriptor().getAttributes();
		String[] paramsToReplace = (String[]) attributes.get("exceptionMsgParams");
		String exceptionCode = (String) attributes.get("exceptionCode");
		new BusinessValidationException(exceptionCode, paramsToReplace).throwFinally();
	}
}