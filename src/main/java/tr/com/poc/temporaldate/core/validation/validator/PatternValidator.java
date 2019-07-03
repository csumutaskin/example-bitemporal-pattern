package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import tr.com.poc.temporaldate.core.annotations.validation.Pattern;

/**
 * BussinesValidator to check whether a field annotated with @Pattern has a matching pattern or not
 * 
 * @author TTKASKIN
 *
 */
public class PatternValidator extends ExistValidator implements ConstraintValidator<Pattern, String>
{
	private String[] method;
	private String pattern;

	@Override
	public void initialize(Pattern constraint)
	{
		this.method = constraint.method();
		this.pattern = constraint.pattern();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context)
	{
		return !isCurrentToBeValidated(method) || value == null || value.matches(pattern);
	}
}
