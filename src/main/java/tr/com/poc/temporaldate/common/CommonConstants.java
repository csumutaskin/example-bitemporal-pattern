package tr.com.poc.temporaldate.common;

/**
 * Micro service based constants reside here
 * 
 * @author umutaskin
 *
 */
public class CommonConstants 
{
	public static final String SCAN_PATH_JPA_ENTITIES = "tr.com";//Root path to scan entities
	public static final String SCAN_PATH_SPRING_COMPONENTS = "tr.com";//Root path to scan spring beans
    public static final String SCAN_PATH_REST_CONTROLLER = "tr.com";//Rest controller path
    
	public static final int    REPOSITORY_BULK_TUPLE_SIZE_BEFORE_FLUSH = 100;
	
	/* Request and Request Header Parameters */
	public static final String REQUEST_HEADER_USERNAME = "username";	
}
