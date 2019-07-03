package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import tr.com.poc.temporaldate.core.annotations.validation.NotEmptyString;

/**
 * BussinesValidator to check whether a field annotated with @NotEmptyString is empty string or not
 * 
 * @author TTKASKIN
 *
 */
public class NotEmptyStringValidator extends ExistValidator implements ConstraintValidator<NotEmptyString, String>
{
	private String[] method;

	@Override
	public void initialize(NotEmptyString constraint)
	{
		this.method = constraint.method();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		return !isCurrentToBeValidated(method) || value == null || StringUtils.isNotBlank(value);
	}
}
