package tr.com.poc.temporaldate.core.validation.validator;

import java.math.BigDecimal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.FieldLength;

/**
 * Created by aziz on 11/27/17.
 */
public class FieldLenghtValidator extends ExistValidator implements ConstraintValidator<FieldLength, Number>
{
	private String[] method;
	private int length;
	private int scale;

	@Override
	public void initialize(FieldLength constraint)
	{
		this.method = constraint.method();
		this.length = constraint.length();
		this.scale = constraint.scale();
	}

	@Override
	public boolean isValid(Number value, ConstraintValidatorContext constraintValidatorContext)
	{
		if (isCurrentToBeValidated(method))
		{
			if (value != null)
			{
				if (value instanceof BigDecimal)
				{
					BigDecimal bd = (BigDecimal) value;
					return !((bd.precision() - bd.scale()) > (length - scale) || bd.scale() > scale);
				}
				else
				{
					String aLong = String.valueOf(value);
					return (aLong.length() <= length);
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
