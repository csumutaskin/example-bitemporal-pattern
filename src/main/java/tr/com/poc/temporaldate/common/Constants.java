package tr.com.poc.temporaldate.common;

public class Constants 
{
	private Constants()
	{}
	public static final String SCAN_PATH_JPA_REPOSITORIES = "tr.com";//Root path to scan entities
	public static final String SCAN_PATH_SPRING_COMPONENTS = "tr.com";//Root path to scan spring beans
	public static final int REPOSITORY_BULK_TUPLE_SIZE_BEFORE_FLUSH = 100;
	public static final String ID_COLUMN_KEY = "id";
	public static final String ID_GETTER_KEY = "getId";
	public static final String ID_SETTER_KEY = "setId";
}
