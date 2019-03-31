package tr.com.poc.temporaldate.core.util.comparator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.util.ExceptionConstants;

/**
 * Date Utility Methods to facilitate date operations
 * @author umutaskin
 *
 */
public class DateUtils 
{
	public static final Date END_OF_SOFTWARE = new GregorianCalendar(2100, 11, 31, 23, 59, 59).getTime();
	
	/**
	 * Checks whether toCompare date is between beginDate and endDate
	 * @param toCompare date to compare
	 * @param beginDate beginning boundary date
	 * @param endDate end boundary date
	 * @param beginEndDatesInclusive -> if "to be checked boundaries" are inclusive make it true, false otherwise
	 * @return true if comparison is true, false otherwise
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
	
	/** 
	 * Calculates and returns the beginning date of the open period e.g. 01 - 03 -2019 for any date in March 2019 	
	 * @param now if true now is returned, else start day of current month is returned (e.g. today is the 16th of March -> returns 1st of March with current Year)
	 * @return Date needed (now or period begin)
	 */
	//TODO: Temporary method, will be filled later using the new business requirements.
	public static Date getNowOrOpenPeriodStartDate(boolean now)
	{
		if(now)
		{
			return new Date();
		}
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}
}
