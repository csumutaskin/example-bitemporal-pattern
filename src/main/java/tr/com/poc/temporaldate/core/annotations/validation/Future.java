package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.FutureValidator;

/**
 * The annotated element must be a Date type. Compares whether the element's value is after now.
 * <p/>
 * Supported types are:
 * <p/>
 * <ul>
 * <li><code>java.util.Date</code></li>
 * <li><code>java.sql.Date</code></li>
 * <li><code>java.sql.TimeStamp</code></li>
 * </ul>
 * <p/>
 * 
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy =
{ FutureValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Future
{
	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0} seems to be a past date but it should have a future time value";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
