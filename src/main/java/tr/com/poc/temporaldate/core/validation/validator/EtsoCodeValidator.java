package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import tr.com.poc.temporaldate.core.annotations.validation.EtsoCode;

/**
 * @author yucelcekinmez
 *
 */
public class EtsoCodeValidator extends ExistValidator implements ConstraintValidator<EtsoCode, String>
{

	private String[] method;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation. Annotation)
	 */
	@Override
	public void initialize(EtsoCode constraintAnnotation)
	{
		this.method = constraintAnnotation.method();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
	 */
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		if (isCurrentToBeValidated(method))
		{
			if (StringUtils.isNotBlank(value))
			{
				String pattern = "^40X[A-Z0-9]{13}$";

				if (value.matches(pattern))
				{
					return true;
				}
				return false;
			}
			else // etsoCodeValue can not be null.
			{
				return false;
			}
		}
		return true;
	}

}
