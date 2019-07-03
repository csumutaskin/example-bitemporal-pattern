package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.NullValidator;

/**
 * The annotated element must be an Object type. Compares whether the element has a null value or not (! if null it is VALID this time !)
 * <p/>
 *
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy =
{ NullValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Null
{
	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0} is not null but it shoul be";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}