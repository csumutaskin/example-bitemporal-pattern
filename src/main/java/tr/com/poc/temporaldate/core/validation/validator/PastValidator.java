package tr.com.poc.temporaldate.core.validation.validator;

import java.util.Calendar;
import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Past;

public class PastValidator extends ExistValidator implements ConstraintValidator<Past, Date>
{
	private String[] method;

	@Override
	public void initialize(Past constraint)
	{
		this.method = constraint.method();
	}

	@Override
	public boolean isValid(Date value, ConstraintValidatorContext context)
	{
		Date now = Calendar.getInstance().getTime();
		return !isCurrentToBeValidated(method) || value != null && value.before(now);
	}
}
