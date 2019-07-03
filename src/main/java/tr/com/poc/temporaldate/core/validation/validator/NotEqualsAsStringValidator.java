package tr.com.poc.temporaldate.core.validation.validator;

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.annotations.validation.NotEqualsAsString;
import tr.com.poc.temporaldate.core.exception.ApplicationException;

public class NotEqualsAsStringValidator extends ExistValidator implements ConstraintValidator<NotEqualsAsString, Object>
{
	private String[] rest;
	private String lookField1;
	private String lookField2;

	@Override
	public void initialize(NotEqualsAsString constraint)
	{
		this.rest = constraint.method();
		this.lookField1 = constraint.lookField1();
		this.lookField2 = constraint.lookField2();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context)
	{
		if (isCurrentToBeValidated(rest))
		{
			if (value != null)
			{
				try
				{
					String value1 = BeanUtils.getProperty(value, lookField1);
					String value2 = BeanUtils.getProperty(value, lookField2);

					if (value1 != null)
					{
						return !value1.equals(value2);
					}
					if (value2 != null)
					{
						return true;
					}
				}
				catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
				{
					throw new ApplicationException(ExceptionConstants.NOT_EQUALS_AS_STRING_VALIDATOR_EXCEPTION);
				}
			}
			else
			{
				return true;
			}
		}
		return true;
	}
}
