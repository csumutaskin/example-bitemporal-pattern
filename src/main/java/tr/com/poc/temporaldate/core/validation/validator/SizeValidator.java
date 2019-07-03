package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Size;

/**
 * BussinesValidator to check whether a field annotated with @Size is within minValue and maxValue Validates only fields of String type. Boundary values are included for validity.
 * 
 * @author TTKASKIN
 *
 */
public class SizeValidator extends ExistValidator implements ConstraintValidator<Size, String>
{
	private String[] method;
	private int minLength;
	private int maxLength;

	@Override
	public void initialize(Size constraint)
	{
		this.method = constraint.method();
		this.minLength = constraint.minLength();
		this.maxLength = constraint.maxLength();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		return !isCurrentToBeValidated(method) || value == null || value.length() >= minLength && value.length() <= maxLength;
	}
}
