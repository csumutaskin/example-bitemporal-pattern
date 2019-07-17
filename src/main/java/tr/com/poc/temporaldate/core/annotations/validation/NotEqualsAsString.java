package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.NotEqualsAsStringValidator;

/**
 * The annotated element must be String type. Checks whether the the lookup fields mutually have the same value. Returns an invalid type message if fields have the same value...
 * <p/>
 * <code>null</code> elements are considered invalid. <code>not null</code> and <code>null</code> fields result to true in not equals validation
 * 
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy ={ NotEqualsAsStringValidator.class })
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEqualsAsString
{
	String lookField1();

	String lookField2();

	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "Following {lookField1} - {lookField2} seem to be equal. Please be sure that they are mutually different";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
