package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.NotNullValidator;

/**
 * The annotated element must be an Object type. Compares whether the element has a null value or not (valid if not null)
 * <p/>
 *
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy =
{ NotNullValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull
{
	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0} is null but it shouldnot be";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}