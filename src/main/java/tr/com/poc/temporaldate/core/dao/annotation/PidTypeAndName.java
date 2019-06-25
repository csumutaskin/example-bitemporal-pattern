package tr.com.poc.temporaldate.core.dao.annotation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * A metadata holder for @Pid annotated fields in a class
 * 
 * @author umutaskin
 *
 */
@AllArgsConstructor
@Getter
@ToString
public class PidTypeAndName
{
	private Class<?> type;
	private String name;
}