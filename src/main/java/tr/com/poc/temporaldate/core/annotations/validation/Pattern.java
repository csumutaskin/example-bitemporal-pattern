package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.PatternValidator;

/**
 * The annotated element must be a String type. Compares whether the element has the given pattern or not
 * <p/>
 * <code>null</code> elements are considered valid.
 *
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy =
{ PatternValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Pattern
{
	String pattern();

	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0} should have a regular expression pattern of {pattern}";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
