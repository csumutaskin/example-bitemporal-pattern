package tr.com.poc.temporaldate.core.validation.validator;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections.CollectionUtils;

import tr.com.poc.temporaldate.core.annotations.validation.NotNullOrNotEmptyList;

/**
 * @author semih
 *
 */
@SuppressWarnings("rawtypes")
public class NotNullOrNotEmptyListValidator extends ExistValidator implements ConstraintValidator<NotNullOrNotEmptyList, Collection>
{
	private String[] method;

	@Override
	public void initialize(NotNullOrNotEmptyList constraintAnnotation)
	{
		this.method = constraintAnnotation.method();
	}

	@Override
	public boolean isValid(Collection value, ConstraintValidatorContext context)
	{
		return !isCurrentToBeValidated(method) || CollectionUtils.isNotEmpty(value);
	}

}
