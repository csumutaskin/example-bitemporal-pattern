package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Max;

/**
 * BussinesValidator to check whether a field annotated with @Max has a maximum value indicated with its "value" property Validates solely fields of Wrapper! type.
 * </p>
 * 
 * @author TTKASKIN
 *
 */
public class MaxValidator extends ExistValidator implements ConstraintValidator<Max, Number>
{
	private String[] method;
	private double maxVal;

	@Override
	public void initialize(Max constraint)
	{
		this.method = constraint.method();
		this.maxVal = constraint.value();
	}

	@Override
	public boolean isValid(Number value, ConstraintValidatorContext context)
	{
		if (isCurrentToBeValidated(method))
		{
			if (value != null)
			{
				if (value.doubleValue() <= maxVal)// if value is within max boundary
				{
					return true;
				}
				else // else false
				{
					return false;
				}
			}
			else// if fields value is null consider as valid
			{
				return true;
			}
		}
		return true;
	}
}
