package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.NotNullOrNotEmptyListValidator;

/**
 * @author semih
 *
 */
@Documented
@Constraint(validatedBy =
{ NotNullOrNotEmptyListValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNullOrNotEmptyList
{
	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0} should be value Format";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
