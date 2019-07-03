package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.EtsoCodeValidator;

/**
 * Marker Validation Annotation for Organiztaion ETSO CODE Strings
 * 
 * @author yucelcekinmez
 */
@Documented
@Constraint(validatedBy =
{ EtsoCodeValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface EtsoCode
{
	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "Etso code is not a valid one";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

}
