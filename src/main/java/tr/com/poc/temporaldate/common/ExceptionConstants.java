package tr.com.poc.temporaldate.common;

public class ExceptionConstants 
{
	private ExceptionConstants() {}
	
	/* **********************************
	 *    Utility Exception Constants
	 ************************************/
	public static final long CUSTOM_COMPARATOR_NOT_EXPECTING_NULL_OBJECT_EXCEPTION = 10000l;
	public static final long DATE_UTILS_NOT_EXPECTING_NULL_OBJECT_COMPARISON_EXCEPTION = 10001l;
	
	/* *************************************
	 *     Converter Exception Constants
	 **************************************/
	public static final long INITIALIZING_NULL_ENTITY_USING_REFLECTION_EXCEPTION = 10200l;
	public static final long INITIALIZING_NULL_DTO_USING_REFLECTION_EXCEPTION = 10201l;
	
	/* *************************************
	 *     @Repository Layer Constants
	 **************************************/
	public static final long GET_ENTITY_LIST_BY_ENTITY_CRITERIA_EXCEPTION = 11001l;	
	public static final long GET_ROW_COUNT_BY_ENTITY_CRITERIA_EXCEPTION = 11002l;
	public static final long GET_ENTITY_LIST_COUNT_BY_ENTITY_CRITERIA_WITH_PAGINATION = 11003l;
	public static final long GET_ENTITY_BY_ENTITY_CRITERIA = 11004l;
	public static final long GET_ENTITY_FOR_UPDATE_BY_ENTITY_CRITERIA = 11005l;
	public static final long GET_ENTITY_LIST_BY_ENTITY_OR_CRITERIA = 11006l;
	public static final long GET_ENTITY_LIST_FOR_UPDATE_BY_ENTITY_CRITERIA = 11007l;
	public static final long SAVE_DTO_RETURN_DTO_EXCEPTION = 11008l;
	public static final long SET_NOT_NULL_AND_NOT_ID_PARAMETERS_OF_SAME_TYPE_EXCEPTION = 11009l;
	public static final long COPY_FROM_BUFFER_TO_DESTINATION_COLLECTION_EXCEPTION = 11010l;
	public static final long COPY_FROM_BUFFER_TO_DESTINATION_OBJECT_EXCEPTION = 11011l;
	public static final long GET_ENTITY_USING_DTO_WITH_ID_EXCEPTION = 11012l;
	public static final long CALL_SETTERS_ON_OBJECT_WITHOUT_NULL_AND_ID_FIELDS_EXCEPTION = 11013l;
	public static final long GET_RELEVANT_CONVERTER_EXCEPTION = 11014l;
	public static final long UPDATE_ENTITY_BY_DTO = 11015l;
		
	public static final long BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_DATE = 11016l;
	public static final long BITEMPORAL_UPDATE_ENTITY = 11017l;
	public static final long BITEMPORAL_GET_ENTITY_AT_EFFECTIVE_TIME_FROM_PERSPECTIVE_TIME = 11018l;
	public static final long BITEMPORAL_GET_ALL_ENTITIES_THAT_INTERSECT_BEGIN_AND_END_DATE = 11019l;
	//getEntityAtEffectiveTimeFromPerspectiveTime
	
	
}
