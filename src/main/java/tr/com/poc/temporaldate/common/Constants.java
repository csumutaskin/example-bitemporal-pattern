package tr.com.poc.temporaldate.common;

public class Constants 
{
	private Constants()
	{}
	
	public static final String NA = "NA";
	
	public static final String SCAN_PATH_JPA_ENTITIES = "tr.com";//Root path to scan entities
	public static final String SCAN_PATH_SPRING_COMPONENTS = "tr.com";//Root path to scan spring beans
	public static final int REPOSITORY_BULK_TUPLE_SIZE_BEFORE_FLUSH = 100;
	public static final String ID_COLUMN_KEY = "id";
	public static final String ID_GETTER_KEY = "getId";
	public static final String ID_SETTER_KEY = "setId";
	
	/* Request and Request Header Parameters */
	public static final String REQUEST_HEADER_USERNAME = "username";
	
	/* MDC Parameters */
	public static final String MDC_CLIENT_IP = "ClientIP";
	public static final String MDC_TRANSACTION_NO = "TransactionNo";
	public static final String MDC_USERNAME = "UserName";
	public static final String MDC_URI = "URI";
	public static final String MDC_HOST_ADDRESS = "HostAddress";
	
	/* Security Parameters */
	public static final String IGNORE_SECURITY_AT_SWAGGER_WEBJARS_URL = "/webjars/**";
	public static final String IGNORE_SECURITY_AT_SWAGGER_CORS_URL = "/swagger-resources/**";
	public static final String IGNORE_SECURITY_AT_ERROR_URL = "/error/**";
	public static final String IGNORE_SECURITY_AT_RESOURCES_URL = "/resources/**";
	
}
