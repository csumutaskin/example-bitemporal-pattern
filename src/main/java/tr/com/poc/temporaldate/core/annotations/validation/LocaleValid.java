package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.LocaleValidator;

/**
 * Compares whether the element can be cast to a valid Java locale
 * <p/>
 * Supported types are:
 * <p/>
 * <ul>
 * <li><code>String</code></li>
 * </ul>
 * <p/>
 * 
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy =
{ LocaleValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface LocaleValid
{
	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0} not seems to be a valid locale.";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
