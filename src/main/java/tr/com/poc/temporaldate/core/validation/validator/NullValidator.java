package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Null;

/**
 * BussinesValidator to check whether a field annotated with @Null is null or not (valid if null)
 * <p/>
 * 
 * @author TTKASKIN
 *
 */
public class NullValidator extends ExistValidator implements ConstraintValidator<Null, Object>
{
	private String[] method;

	@Override
	public void initialize(Null constraint)
	{
		this.method = constraint.method();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
        return !isCurrentToBeValidated(method) || value == null;
    }
}
