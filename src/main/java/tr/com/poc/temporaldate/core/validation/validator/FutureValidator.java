package tr.com.poc.temporaldate.core.validation.validator;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Future;

/**
 * BussinesValidator to check whether a field annotated with Date is after now or not.
 * </p>
 * 
 * @author TTKASKIN
 *
 */
public class FutureValidator extends ExistValidator implements ConstraintValidator<Future, Date>
{
	private String[] method;

	@Override
	public void initialize(Future constraint)
	{
		this.method = constraint.method();
	}

	@Override
	public boolean isValid(Date value, ConstraintValidatorContext context)
	{
		if (isCurrentToBeValidated(method))
		{
			Date now = Calendar.getInstance().getTime();
			if (value == null)
			{
				return false;
			}
			if (value.after(now))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
}
