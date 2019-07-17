package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.SizeValidator;

/**
 * The annotated element must be a String type. Compares whether the element's string length is within minValue and maxValue (boundary values are included for validity)
 * <p/>
 * <code>null</code> elements are considered valid.
 * 
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy =
{ SizeValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Size
{
	int minLength() default 0;

	int maxLength() default Integer.MAX_VALUE;

	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0}'s length seems to be out of the {minLength} - {maxLength} boundaries!";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
