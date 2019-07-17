package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.NotNull;

/**
 * BussinesValidator to check whether a field annotated with @NotNull is null or not
 * <p/>
 * 
 * @author TTKASKIN
 *
 */
public class NotNullValidator extends ExistValidator implements ConstraintValidator<NotNull, Object>
{
	private String[] method;

	@Override
	public void initialize(NotNull constraint)
	{
		this.method = constraint.method();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context)
	{
		return !isCurrentToBeValidated(method) || value != null;
	}
}
