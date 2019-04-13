package tr.com.poc.temporaldate.core.util.comparator;

import java.util.Comparator;
import java.util.Date;

import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.model.bitemporal.BaseBitemporalEntity;

/**
 * Sorts Any bitemporal entity by effective start date
 * @author umutaskin
 */
public class SortBaseEntityByEffectiveStartDateComparator<E extends BaseBitemporalEntity> implements Comparator<E> 
{
	@Override
	public int compare(E o1, E o2) 
	{
		if(o1 == null || o2 == null)
		{
			//TODO: get message from propery file with dynamic parameters appended to log
			throw new ApplicationException(ExceptionConstants.CUSTOM_COMPARATOR_NOT_EXPECTING_NULL_OBJECT_EXCEPTION, "One of the items compared using SortBaseEntityByEffectiveStartDateComparator is null, none of the items should be null", new NullPointerException());
		}		
		
		Date o1Start = o1.getEffectiveDateStart();
		Date o2Start = o2.getEffectiveDateStart();
				
		if(o1Start == null || o2Start == null)
		{
			//TODO: get message from propery file with dynamic parameters appended to log
			throw new ApplicationException(ExceptionConstants.CUSTOM_COMPARATOR_NOT_EXPECTING_NULL_OBJECT_EXCEPTION, "One of the item's date compared using SortBaseEntityByEffectiveStartDateComparator is null, none of the items should be null", new NullPointerException());
		}
		return o1Start.compareTo(o2Start);
	}
}
