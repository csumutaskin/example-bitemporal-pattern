package tr.com.poc.temporaldate.common;

import java.util.Locale;

/**
 * Contains all application scoped and reusable constants
 * 
 * @author umutaskin
 *
 */
public class Constants 
{
	private Constants()
	{}
	
	public static final Locale LOCALE_TR = new Locale("tr-TR");
	public static final String UNDEFINED_STR = "undefined";
	
	public static final String PROFILE_DEV = "dev";
	public static final String PROFILE_QA = "qa";
	public static final String PROFILE_UAT = "uat";
	public static final String PROFILE_PREPROD = "preprod";
	public static final String PROFILE_PROD = "prod";	
	
	public static final String NA = "NA";
	public static final String UTF8 = "UTF-8";
	public static final String STARTUP = "STARTUP";
	
	public static final String SCAN_PATH_JPA_ENTITIES = "tr.com";//Root path to scan entities
	public static final String SCAN_PATH_SPRING_COMPONENTS = "tr.com";//Root path to scan spring beans
	public static final String CLASSPATH_FOR_EXCEPTION_PROPERTIES = "classpath:" + "/exception/";//classpath where application and business exception properties reside
	
	public static final int    REPOSITORY_BULK_TUPLE_SIZE_BEFORE_FLUSH = 100;
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
	
	/* Security and Security byPass Parameters */
	public static final String IGNORE_SECURITY_AT_SWAGGER_WEBJARS_URL = "/webjars/**";
	public static final String IGNORE_SECURITY_AT_SWAGGER_URL = "/swagger*/**";
	public static final String IGNORE_SECURITY_AT_SWAGGER_API_DOCS_URL = "/v2/api-docs";
	public static final String IGNORE_SECURITY_AT_ERROR_URL = "/error/**";
	public static final String IGNORE_SECURITY_AT_RESOURCES_URL = "/resources/**";
	public static final String IGNORE_SECURITY_AT_H2_URL = "/h2-console/**";
	
	
	
	/* Message Bundle File Parameters */
	public static final String MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS = "application-exception"; //application-exception_LOCALE.properties
	public static final String MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS = "business-exception"; //business-exception_LOCALE.properties
	public static final String MESSAGE_DEAFULT_FOR_BUSINESS_EXCEPTIONS_FOR_NOT_FOUND_ERROR_CODES = "Sistemde ilgili hatanın açıklaması yer almamaktadır. Lütfen hata kodunuzu ve işlem numaranızı kullanarak sistem yöneticilerine başvurunuz."; //business-exception_LOCALE.properties
	public static final String MESSAGE_DEAFULT_FOR_BUSINESS_EXCEPTIONS_FOR_NOT_FOUND_ERROR_CODES_EN = "There is no error explanation present in the system for the exception you recieved, please contact system administrators with your transaction number and exception code";
	public static final String BUSINESS_VALIDATION_EXCEPTION_PREFIX = " (!) BUSINESS VALIDATION EXCEPTION (!) : ";
	public static final String MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS_DEFAULT_PATH = "exception/application-exception.properties";
	public static final String MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS_EN_PATH = "exception/application-exception_en.properties";
	public static final String MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS_DEFAULT_PATH = "exception/business-exception.properties";
	public static final String MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS_EN_PATH = "exception/business-exception_en.properties";
	
	/* Aspect Parameters */
	public static final String EXECUTION_OF_CONTROLLER_METHODS = "execution(public * tr.com.poc.temporaldate.*.controller.*.*(..))";
}
