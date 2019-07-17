package tr.com.poc.temporaldate.core.annotations.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import tr.com.poc.temporaldate.core.validation.validator.MaxValidator;

/**
 * The annotated element must be a Wrapper Object of type Number. Compares the element's actual value with the boundary given in annotation's value property
 * <p/>
 * Supported types are:
 * <ul>
 * <li><code>Number</code></li>
 * <li><code>AtomicInteger</code></li>
 * <li><code>AtomicLong</code></li>
 * <li><code>BigDecimal</code></li>
 * <li><code>BigInteger</code></li>
 * <li><code>Byte</code></li>
 * <li><code>Double</code></li>
 * <li><code>Float</code></li>
 * <li><code>Integer</code></li>
 * <li><code>Long</code></li>
 * <li><code>Short</code></li>
 * </ul>
 * <p/>
 * <code>null</code> elements are considered valid.
 *
 * @author Umut Askin
 */
@Documented
@Constraint(validatedBy =
{ MaxValidator.class })
@Target(
{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Max
{
	String[] method() default {};

	String[] exceptionMsgParams() default {};

	String message() default "{0}'s maximum value can be {value}";

	String exceptionCode() default "-1";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};

	double value() default 0.0d;

	/**
	 * Defines several <code>@Max</code> annotations on the same element
	 * 
	 * @see Max
	 * @author Umut Askin
	 */
	@Target(
	{ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface MaxList
	{
		Max[] value();
	}
}