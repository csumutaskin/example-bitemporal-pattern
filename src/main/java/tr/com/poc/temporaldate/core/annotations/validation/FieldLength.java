package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.FieldLenghtValidator;

/**
 * Created by aziz on 11/27/17.
 */
@Documented
@Constraint(validatedBy =
{ FieldLenghtValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldLength
{

	int length() default 0;

	int scale() default 0;

	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0}'s length seems to be out of the {length} - {scale} boundaries!";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
