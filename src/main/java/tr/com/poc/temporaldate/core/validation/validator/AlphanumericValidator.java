package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Alphanumeric;

/**
 * BussinesValidator to check whether a field annotated with Alphanumeric is empty string or not
 * </p>
 * 
 * @author TTKASKIN
 *
 */
public class AlphanumericValidator extends ExistValidator implements ConstraintValidator<Alphanumeric, String>
{
	private String[] method;

	@Override
	public void initialize(Alphanumeric constraint)
	{
		this.method = constraint.method();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		if (isCurrentToBeValidated(method))
		{
			if (value != null)
			{
				String pattern = "^[a-zA-Z0-9]*$";
				if (value.matches(pattern))
				{
					return true;
				}
				return false;
			}
			else // if value is null then it can be considered as alphanumeric
			{
				return true;
			}
		}
		return true;
	}
}
