package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import tr.com.poc.temporaldate.core.validation.BussinesValidator;

/**
 * <br>
 * An implementation of a valid annotation to mark which parameters are to be validated <br>
 * by JSR validation. This marker annotation should be used on service class' methods' <br>
 * input parameters, to force them to be validated with the given validation <br>
 * annotations like @notnull, @notblank, @size.. and others in rest package.
 * 
 * @see {@link NotNull}, {@link NotBlank}, {@link Size} and the others in annotation package
 * 
 * @author umutaskin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.PARAMETER })
public @interface Valid
{
	Class<?>[] groups() default {};

	Class<? extends BussinesValidator>[] validator() default {};

	boolean throwManually() default false;
}