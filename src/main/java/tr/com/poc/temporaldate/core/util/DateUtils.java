package tr.com.poc.temporaldate.core.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;

/**
 * Date Utility Methods to facilitate date operations
 * 
 * @author umutaskin
 *
 */
public final class DateUtils 
{
	private DateUtils() {}
	
	//public static final Date END_OF_SOFTWARE  = new GregorianCalendar(2100, 11, 31, 23, 59, 59).getTime();
	public static final LocalDateTime END_OF_SOFTWARE  = LocalDateTime.of(2100, 12, 31, 23, 59, 59);

	/**
	 * Checks whether toCompare date is between beginDate and endDate
	 * @param toCompare date to compare
	 * @param beginDate beginning boundary date
	 * @param endDate end boundary date
	 * @param beginEndDatesInclusive -> if "to be checked boundaries" are inclusive make it true, false otherwise
	 * @return true if comparison is true, false otherwise
	 */
	//public static boolean dateBetweenDates(Date toCompare, Date beginDate, Date endDate, boolean beginEndDatesInclusive)
	public static boolean dateBetweenDates(LocalDateTime toCompare, LocalDateTime beginDate, LocalDateTime endDate, boolean beginEndDatesInclusive)
	{
		if(toCompare == null || beginDate == null || endDate == null)
		{
			//TODO: get log message with dynamic parameters from property file
			throw new ApplicationException(ExceptionConstants.DATE_UTILS_NOT_EXPECTING_NULL_OBJECT_COMPARISON_EXCEPTION , new NullPointerException());
		}
		int comparisonResult = beginDate.compareTo(toCompare) * toCompare.compareTo(endDate);
		if(beginEndDatesInclusive)
		{
			return comparisonResult >= 0;
		}
		return comparisonResult > 0;		
	}
	
//	/** 
//	 * Returns "now" or calculates and returns the beginning of the given period which is trimmed using trim type
//	 * </br> <b>e.g.</b> <i>03 - 01 -2019 for any date in March 2019</i> 	
//	 * @param alwaysGetNowAsDate true: now is returned
//	 * @param trimType 
//	 * @return Date needed (now or monthly period begin)
//	 */
//	public static Date getNowOrGivenOrOpenPeriodStartDate(Trim trimType)
//	{
//		Calendar c = Calendar.getInstance();
//		switch(trimType)
//		{
//			case MILISECOND:
//				c.set(Calendar.MILLISECOND, 0);
//				break;
//			case SECOND:
//				c.set(Calendar.SECOND, 0);
//				c.set(Calendar.MILLISECOND, 0);
//				break;
//			case MINUTE:
//				c.set(Calendar.MINUTE, 0);
//				c.set(Calendar.SECOND, 0);
//				c.set(Calendar.MILLISECOND, 0);
//				break;
//			case HOUR:
//				c.set(Calendar.HOUR_OF_DAY, 0);
//				c.set(Calendar.MINUTE, 0);
//				c.set(Calendar.SECOND, 0);
//				c.set(Calendar.MILLISECOND, 0);
//				break;
//			case DAY:
//				c.set(Calendar.DAY_OF_MONTH, 1);		
//				c.set(Calendar.HOUR_OF_DAY, 0);
//				c.set(Calendar.MINUTE, 0);
//				c.set(Calendar.SECOND, 0);
//				c.set(Calendar.MILLISECOND, 0);
//				break;
//			case MONTH:
//				c.set(Calendar.MONTH, Calendar.JANUARY);
//				c.set(Calendar.DAY_OF_MONTH, 1);		
//				c.set(Calendar.HOUR_OF_DAY, 0);
//				c.set(Calendar.MINUTE, 0);
//				c.set(Calendar.SECOND, 0);
//				c.set(Calendar.MILLISECOND, 0);
//				break;
//			case NOW:
//			default:
//				//return now.
//				break;
//		}
//		return c.getTime();
//	}
	
	/** 
	 * Returns "now" or calculates and returns the beginning of the given period which is trimmed using trim type
	 * </br> <b>e.g.</b> <i>03 - 01 -2019 for any date in March 2019</i> 	
	 * @param alwaysGetNowAsDate true: now is returned
	 * @param trimType 
	 * @return {@link LocalDateTime} needed (now or monthly period begin)
	 */
	//TODO: LocalDateTime librarisini test et...
	public static LocalDateTime getNowOrGivenOrOpenPeriodStartDate(Trim trimType)
	{
		LocalDateTime toReturn = null;
		LocalDateTime now = LocalDateTime.now();
		switch(trimType)
		{
			case MILISECOND:
				toReturn = now.truncatedTo(ChronoUnit.MILLIS);
				break;
			case SECOND:
				toReturn = now.truncatedTo(ChronoUnit.SECONDS);
				break;
			case MINUTE:
				toReturn = now.truncatedTo(ChronoUnit.MINUTES);				
				break;
			case HOUR:
				toReturn = now.truncatedTo(ChronoUnit.DAYS);
				break;
			case DAY:
				toReturn = now.truncatedTo(ChronoUnit.MONTHS);
				break;
			case MONTH:
				toReturn = now.truncatedTo(ChronoUnit.YEARS);				
				break;
			case NOW:
			default:
				toReturn = now;
				break;
		}
		return toReturn;
	}
}
