package tr.com.poc.temporaldate.util;

import java.util.Date;
import java.util.GregorianCalendar;

public class Constants 
{
	private Constants()
	{}
	
	public static final Date END_OF_EPYS = new GregorianCalendar(2050, 0, 1, 0, 0, 0).getTime();
	
	public static final String SCAN_PATH_JPA_REPOSITORIES = "tr.com";
	public static final int REPOSITORY_BULK_TUPLE_SIZE_BEFORE_FLUSH = 100;
	public static final String ID_COLUMN_KEY = "id";
	public static final String ID_GETTER_KEY = "getId";
	public static final String ID_SETTER_KEY = "setId";
}
