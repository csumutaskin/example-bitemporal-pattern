package tr.com.poc.temporaldate.common;

public class ExceptionConstants 
{
	private ExceptionConstants() {}
	
	public static final String UNEXPECTED_EXCEPTION = "00001";
	
	/* **********************************
	 *    Utility Exception Constants
	 ************************************/
	public static final String CUSTOM_COMPARATOR_NOT_EXPECTING_NULL_OBJECT_EXCEPTION = "10000";
	public static final String DATE_UTILS_NOT_EXPECTING_NULL_OBJECT_COMPARISON_EXCEPTION = "10001";
	
	/* *************************************
	 *     Converter Exception Constants
	 **************************************/
	public static final String INITIALIZING_NULL_ENTITY_USING_REFLECTION_EXCEPTION = "10200";
	public static final String INITIALIZING_NULL_DTO_USING_REFLECTION_EXCEPTION = "10201";
	
	/* *************************************
	 *     @Repository Layer Constants
	 **************************************/
	public static final String GET_ENTITY_LIST_BY_ENTITY_CRITERIA_EXCEPTION = "11001";	
	public static final String GET_ROW_COUNT_BY_ENTITY_CRITERIA_EXCEPTION = "11002";
	public static final String GET_ENTITY_LIST_COUNT_BY_ENTITY_CRITERIA_WITH_PAGINATION = "11003";
	public static final String GET_ENTITY_BY_ENTITY_CRITERIA = "11004";
	public static final String GET_ENTITY_FOR_UPDATE_BY_ENTITY_CRITERIA = "11005";
	public static final String GET_ENTITY_LIST_BY_ENTITY_OR_CRITERIA = "11006";
	public static final String GET_ENTITY_LIST_FOR_UPDATE_BY_ENTITY_CRITERIA = "11007";
	public static final String SAVE_DTO_RETURN_DTO_EXCEPTION = "11008";
	public static final String SET_NOT_NULL_AND_NOT_ID_PARAMETERS_OF_SAME_TYPE_EXCEPTION = "11009";
	public static final String COPY_FROM_BUFFER_TO_DESTINATION_COLLECTION_EXCEPTION = "11010";
	public static final String COPY_FROM_BUFFER_TO_DESTINATION_OBJECT_EXCEPTION = "11011";
	public static final String GET_ENTITY_USING_DTO_WITH_ID_EXCEPTION = "11012";
	public static final String CALL_SETTERS_ON_OBJECT_WITHOUT_NULL_AND_ID_FIELDS_EXCEPTION = "11013";
	public static final String GET_RELEVANT_CONVERTER_EXCEPTION = "11014";
	public static final String UPDATE_ENTITY_BY_DTO = "11015";
		
	public static final String BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_DATE = "11016";
	public static final String BITEMPORAL_UPDATE_ENTITY = "11017";
	public static final String BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_TIME_FROM_PERSPECTIVE_TIME = "11018";
	public static final String BITEMPORAL_GET_ALL_ENTITIES_THAT_StringERSECT_BEGIN_AND_END_DATE = "11019";
	//getEntityAtEffectiveTimeFromPerspectiveTime
	
	
}