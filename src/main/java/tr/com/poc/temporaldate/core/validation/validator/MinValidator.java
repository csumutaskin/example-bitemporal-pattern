package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Min;

/**
 * BussinesValidator to check whether a field annotated with @Min has a maximum value indicated with its "value" property Validates solely fields of Wrapper! type.
 * </p>
 * 
 * @author TTKASKIN
 *
 */
public class MinValidator extends ExistValidator implements ConstraintValidator<Min, Number>
{
	private String[] method;
	private double minVal;

	@Override
	public void initialize(Min constraint)
	{
		this.method = constraint.method();
		this.minVal = constraint.value();
	}

	@Override
	public boolean isValid(Number value, ConstraintValidatorContext context)
	{
		return !isCurrentToBeValidated(method) || value == null || value.doubleValue() >= minVal;
	}
}
