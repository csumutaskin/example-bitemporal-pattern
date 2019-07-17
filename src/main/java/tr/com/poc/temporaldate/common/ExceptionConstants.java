package tr.com.poc.temporaldate.common;

/**
 * Contains all application scoped and reusable exception code constants
 * 
 * @author umutaskin
 *
 */
public class ExceptionConstants 
{
	private ExceptionConstants() {}
	
	public static final String APPLICATION_ERROR_PREFIX = "APP";
	public static final String BUSINESS_ERROR_PREFIX = "BUS";
	public static final String VALIDATION_ERROR_PREFIX = "VAL";
		
	public static final String UNEXPECTED = "00001";
	public static final String UNEXPECTED_OBJECT_CREATION_EXCEPTION_THROUGH_REFLECTION = "00002";
	public static final String USER_INPUT_NOT_VALIDATED = "99999";
	
	/* **********************************
	 *    Utility Exception Constants
	 ************************************/
	public static final String CUSTOM_COMPARATOR_NOT_EXPECTING_NULL_OBJECT_EXCEPTION = "10000";
	public static final String DATE_UTILS_NOT_EXPECTING_NULL_OBJECT_COMPARISON_EXCEPTION = "10001";
	
	/* *************************************
	 *     Converter and Cloning Exception Constants
	 **************************************/
	public static final String INITIALIZING_NULL_ENTITY_USING_REFLECTION_EXCEPTION = "10200";
	public static final String INITIALIZING_NULL_DTO_USING_REFLECTION_EXCEPTION = "10201";
	public static final String CLONING_ENTITY_EXCEPTION = "10202";
	public static final String NOT_CONVERTABLE_ENTITY_EXCEPTION = "10203";
	
	/* *************************************
	 *     Server startup Exception Constants
	 **************************************/
	public static final String SERVER_STARTUP_EXCEPTION = "10300";
	
	/* *************************************
	 *     @Repository Layer Constants
	 **************************************/
	
	//READ Exception Constants
	public static final String GET_ENTITY_LIST_BY_ENTITY_CRITERIA_EXCEPTION = "11001";	
	public static final String GET_ROW_COUNT_BY_ENTITY_CRITERIA_EXCEPTION = "11002";
	public static final String GET_ENTITY_LIST_COUNT_BY_ENTITY_CRITERIA_WITH_PAGINATION = "11003";
	public static final String GET_ENTITY_BY_ENTITY_CRITERIA = "11004";
	public static final String GET_ENTITY_FOR_UPDATE_BY_ENTITY_CRITERIA = "11005";
	public static final String GET_ENTITY_LIST_BY_ENTITY_OR_CRITERIA = "11006";
	public static final String GET_ENTITY_LIST_FOR_UPDATE_BY_ENTITY_CRITERIA = "11007";
	public static final String SET_NOT_NULL_AND_NOT_ID_PARAMETERS_OF_SAME_TYPE_EXCEPTION = "11008";
	public static final String COPY_FROM_BUFFER_TO_DESTINATION_COLLECTION_EXCEPTION = "11009";
	public static final String COPY_FROM_BUFFER_TO_DESTINATION_OBJECT_EXCEPTION = "11010";
	public static final String GET_ENTITY_USING_DTO_WITH_ID_EXCEPTION = "11011";
	public static final String CALL_SETTERS_ON_OBJECT_WITHOUT_NULL_AND_ID_FIELDS_EXCEPTION = "11012";
	public static final String GET_RELEVANT_CONVERTER_EXCEPTION = "11013";
	public static final String BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_DATE = "11014";
	public static final String BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_TIME_FROM_OBSERVER_TIME = "11015";
	public static final String BITEMPORAL_GET_ALL_ENTITIES_THAT_INTERSECT_BEGIN_AND_END_DATE = "11016";
	public static final String BITEMPORAL_ENTITY_SELECTION_WITH_NO_PID_OR_NO_PID_TYPE = "11017";
	public static final String BITEMPORAL_PID_NOT_SERIALIZABLE_FIELD = "11018";
	public static final String BITEMPORAL_GET_ALL_ENTITIES_WITH_NULL_PID = "11019";	
	public static final String BITEMPORAL_PERSISTED_OR_UPDATED_ENTITIES_ALL_4_DATES_NOT_EXIST = "11020";
	public static final String BITEMPORAL_ENTITY_HAS_NO_PID_COLUMN_THAT_CAN_BE_USED_IN_PID_UTILITY = "11021";
	public static final String BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_OBSERVER_END_BEFORE_OBSERVER_BEGIN = "11022";
	public static final String BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_EFFECTIVE_END_BEFORE_EFFECTIVE_BEGIN = "11023";
	public static final String BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_OBSERVER_END_BEFORE_END_OF_SOFTWARE = "11024";
	public static final String BITEMPORAL_PERSISTED_ENTITY_PID_COLUMN_SHOULD_BE_NULL = "11025";
	public static final String BITEMPORAL_PERSISTED_ENTITY_PID_COLUMN_VALUE_CAN_NOT_BE_REACHED = "11026";
	public static final String BITEMPORAL_PERSISTED_ENTITY_PID_COLUMN_VALUE_CAN_NOT_BE_SET = "11027";
	public static final String BITEMPORAL_PERSISTED_OR_UPDATED_ENTITY_CANNOT_BE_NULL = "11028";
	public static final String BITEMPORAL_ENTITY_CLASS_CAN_NOT_BE_NULL = "11029";
	public static final String BITEMPORAL_ENTITY_PID_UTILITY_CAN_NOT_BE_APPLIED_TO_NULL_VALUED_OBJECT = "11030";
	public static final String BITEMPORAL_UPDATE_PID_UTILITY_CAN_NOT_DETECT_ANY_PRIOR_TUPLES_TO_UPDATE = "11031";
	public static final String BITEMPORAL_PERSISTED_ENTITY_EFFECTIVE_END_BEFORE_END_OF_SOFTWARE = "11032";
	public static final String BITEMPORAL_PERSISTE_OR_UPDATE_GAP_CONTROL_PID_CAN_NOT_BE_NULL = "11033";
	public static final String BITEMPORAL_PERSISTE_OR_UPDATE_GAP_EXISTS_BEFORE_EFFECTIVE_START_DATE = "11034";
	public static final String BITEMPORAL_PERSISTE_OR_UPDATE_GAP_EXISTS_AFTER_EFFECTIVE_END_DATE = "11035";
		
	//CREATE Exception Constants
	public static final String NULL_OBJECT_CAN_NOT_BE_SAVED_EXCEPTION = "11200";
	public static final String SAVE_DTO_RETURN_DTO_EXCEPTION = "11201";
	
	//UPDATE Exception Constants
	public static final String UPDATE_ENTITY_BY_DTO = "11400";
	public static final String BITEMPORAL_UPDATE_ENTITY = "11401";
			
	//getEntityAtEffectiveTimeFromObserverTime
}