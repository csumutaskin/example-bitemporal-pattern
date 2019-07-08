package tr.com.poc.temporaldate.core.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Entity column that represent natural id for a given entity 
 * Same pid value in a &#64;pid column can be repeated several times in the same table. 
 * 
 * @author umutaskin
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Pid 
{
	public String sequenceName();
}
