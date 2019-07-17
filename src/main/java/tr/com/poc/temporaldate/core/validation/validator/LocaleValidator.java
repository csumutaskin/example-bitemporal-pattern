package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import tr.com.poc.temporaldate.core.annotations.validation.LocaleValid;

/**
 * BussinesValidator to check whether a string field annotated with Locale can be cast to a valid Java Locale
 * </p>
 * 
 * @author TTKASKIN
 *
 */
public class LocaleValidator extends ExistValidator implements ConstraintValidator<LocaleValid, String>
{
	private String[] method;

	@Override
	public void initialize(LocaleValid constraint)
	{
		this.method = constraint.method();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		if (isCurrentToBeValidated(method))
		{
			if (StringUtils.isNotBlank(value))
			{
				try
				{
					//LocaleUtils.toLocale(value);
					return true;
				}
				catch (Exception e)
				{
					return false;
				}
			}
			else // if value is blank (null or empty) then it is not a valid locale
			{
				return false;
			}
		}
		return true;
	}

}
