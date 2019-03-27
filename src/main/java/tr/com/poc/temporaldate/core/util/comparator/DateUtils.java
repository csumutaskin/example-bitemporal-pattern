package tr.com.poc.temporaldate.core.util.comparator;

import java.util.Date;

import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.util.ExceptionConstants;

/**
 * Date Utility Methods to facilitate date operations
 * @author umutaskin
 *
 */
public class DateUtils 
{
	/**
	 * Checks whether toCompare date is between beginDate and endDate
	 * @param toCompare date to compare
	 * @param beginDate beginning boundary date
	 * @param endDate end boundary date
	 * @param beginEndDatesInclusive -> if "to be checked boundaries" are inclusive make it true, false otherwise
	 * @return
	 */
	public static boolean dateBetweenDates(Date toCompare, Date beginDate, Date endDate, boolean beginEndDatesInclusive)
	{
		if(toCompare == null || beginDate == null || endDate == null)
		{
			//TODO: get log message with dynamic parameters from property file
			throw new ApplicationException(ExceptionConstants.DATE_UTILS_NOT_EXPECTING_NULL_OBJECT_COMPARISON_EXCEPTION , "At lease one of the date fields compared is null", new NullPointerException());
		}
		int comparisonResult = beginDate.compareTo(toCompare) * toCompare.compareTo(endDate);
		if(beginEndDatesInclusive)
		{
			return comparisonResult >= 0;
		}
		return comparisonResult > 0;		
	}
}
