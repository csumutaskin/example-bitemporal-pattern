package tr.com.poc.temporaldate.core.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import tr.com.poc.temporaldate.core.annotations.validation.NotBlank;

/**
 * @author semih
 *
 */
public class NotBlankValidator extends ExistValidator implements ConstraintValidator<NotBlank, String>
{
	private String[] method;

	@Override
	public void initialize(NotBlank constraintAnnotation)
	{
		this.method = constraintAnnotation.method();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
        return !isCurrentToBeValidated(method) || StringUtils.isNotBlank(value);
    }

}
