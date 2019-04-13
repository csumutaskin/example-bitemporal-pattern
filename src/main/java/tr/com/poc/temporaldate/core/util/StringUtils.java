package tr.com.poc.temporaldate.core.util;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.util.CollectionUtils;

public class StringUtils 
{
	private StringUtils() {}
	
	//TODO: check!
	public static String toStringCollection(Collection<?> collection)
	{
		StringBuilder toReturn = new StringBuilder();
		if(CollectionUtils.isEmpty(collection))
		{
			toReturn.append("Null or Empty Collection");
		}
		Iterator<?> iterator = collection.iterator();
		while(iterator.hasNext())
		{
			toReturn.append("[" + iterator.toString() + "], ");
		}
		int sbLength = toReturn.length();
		if(sbLength > 0 && toReturn.lastIndexOf(",") == sbLength - 1)
		{
			return toReturn.substring(0, toReturn.length() - 1);
		}
		return toReturn.toString();
	}
}
