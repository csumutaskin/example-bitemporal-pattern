package tr.com.poc.temporaldate.core.util;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;

/**
 * Project specific String Utility Methods reside here... 
 * 
 * @author umutaskin
 *
 */
public class StringUtils 
{
	private StringUtils() {}
	
	/**
	 * Converts any collection to comma separated string
	 * @param collection collection to be converted
	 * @return {@link String}
	 */
	public static String toStringCollection(Collection<?> collection)
	{
		String toReturn = null;
		if(CollectionUtils.isEmpty(collection))
		{
			toReturn = "[Null or Empty Collection]";
		}
		else
		{
			toReturn = collection.stream().map(Object::toString).collect(Collectors.joining(", "));
		}
		return toReturn;
	}
}
