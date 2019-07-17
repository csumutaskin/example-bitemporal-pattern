package tr.com.poc.temporaldate.core.dao.annotation;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.lang3.exception.ExceptionUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.exception.ApplicationException;

/**
 * A metadata holder for &#64;Pid annotated fields in a class
 * 
 * @author umutaskin
 *
 */
@AllArgsConstructor
@Getter
@ToString
@Log4j2
public class PidDetail
{
	private Class<?> type;
	private String name;
	private String sequenceName;
	private Field field;
	
	/**
	 * Calls the getter of Pid annotated column's getter in an entity if exists, otherwise throws related exceptions
	 * @param pidColumnOwnerClassType Class type which owns the @Pid column data (not the type of the @Pid column)
	 * 		  <p>
	 *  	  <b><i>e.g.</i></b>
	 *  	  </br>
	 *  	  <code> 
	 *  		class Organization</br>
	 *  		{</br>
	 *  			&#64;Pid</br>
	 *  			String sampleColumn;</br>
	 *  		}</br>
	 *  	  </code>
	 *  	  <p>
	 *  	  <i>Call as: readPidColumnValue(<b><i>Organization.class</b></i>, ...);</i>
	 * @param objectToReadFrom the actual object whose getter method is called
	 * @author umutaskin 
	 * @return {@link Object} Explicitly cast the result to the related type.
	 */
	public Object readPidColumnValue(Class<?> pidColumnOwnerClassType, Object objectToReadFrom)
	{	
		String typeStrOfObjectToReadFrom = "NA";
		if(pidColumnOwnerClassType != null)
		{
			typeStrOfObjectToReadFrom = pidColumnOwnerClassType.getSimpleName();
		}
		if(objectToReadFrom == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_ENTITY_PID_UTILITY_CAN_NOT_BE_APPLIED_TO_NULL_VALUED_OBJECT, typeStrOfObjectToReadFrom);
		}
		try 
		{			
			return this.getField().get(objectToReadFrom);			
		} 
		catch (IllegalArgumentException | IllegalAccessException e) 
		{				
			log.error("While persisting an entity with @Pid utility, can not read @Pid's value of object of type: {} entity, no save will be made.", typeStrOfObjectToReadFrom);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_ENTITY_PID_COLUMN_VALUE_CAN_NOT_BE_REACHED, e, typeStrOfObjectToReadFrom, objectToReadFrom.toString());
		}
	}
	
	/**
	 * Calls the setter of &#64;Pid annotated column's getter in an entity if exists, otherwise throws related exceptions
	 * @param pidColumnOwnerClassType Class type which owns the @Pid column data (not the type of the @Pid column)
	 * 		  <p>
	 *  	  <b><i>e.g.</i></b>
	 *  	  </br>
	 *  	  <code> 
	 *  		class Organization</br>
	 *  		{</br>
	 *  			&#64;Pid</br>
	 *  			String sampleColumn;</br>
	 *  		}</br>
	 *  	  </code>
	 *  	  <p>
	 *  	  <i>Call as:setPidColumnValue(<b><i>Organization.class</b></i>, ...);</i> 
	 * @param objectToSetTo the actual object whose setter method is called
	 * @param valueToSet value to set to @Pid annotated column
	 * @author umutaskin 
	 */
	public void setPidColumnValue(Class<?> pidColumnOwnerClassType, Object objectToSetTo, Object valueToSet)
	{	
		String typeStrOfObjectToSetTo = "NA";
		if(pidColumnOwnerClassType != null)
		{
			typeStrOfObjectToSetTo = pidColumnOwnerClassType.getSimpleName();
		}
		if(objectToSetTo == null)
		{
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_ENTITY_PID_UTILITY_CAN_NOT_BE_APPLIED_TO_NULL_VALUED_OBJECT, typeStrOfObjectToSetTo);
		}		
		try 
		{
			Object castedVal = type.getDeclaredConstructor(String.class).newInstance(valueToSet.toString());
			this.getField().set(objectToSetTo, castedVal);
		} 
		catch (IllegalArgumentException|IllegalAccessException|InstantiationException|InvocationTargetException | NoSuchMethodException | SecurityException e) 
		{
			log.error("While persisting an entity with @Pid utility, can not set @Pid's value of object of type: {} with {} sequence current value {}, no save will be made. ", typeStrOfObjectToSetTo, sequenceName, valueToSet);
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_PERSISTED_ENTITY_PID_COLUMN_VALUE_CAN_NOT_BE_SET, e, typeStrOfObjectToSetTo, objectToSetTo.toString());
		}
	}
	
	/**
	 * Casts a serializable value to the @Pid column's data type using reflection
	 * @param pid serializable value to be casted to
	 * @return {@link Object}
	 */
	public Object castGivenValueToPidType(Class<?> pidColumnOwnerClassType, Serializable pid)
	{
		String ownerClassType = "NA";
		if(pidColumnOwnerClassType != null)
		{
			ownerClassType = pidColumnOwnerClassType.getSimpleName();
		}
		Object toReturnCastedObject = null;
		try 
		{
			toReturnCastedObject = type.getDeclaredConstructor(String.class).newInstance(pid.toString());			
		} 
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) 
		{
			log.error("Can not cast Serializable pid:(value) {} to type: {}, exception detail: {} ", pid, type.toString(), ExceptionUtils.getStackTrace(e));
			throw new ApplicationException(ExceptionConstants.BITEMPORAL_GET_ALL_ENTITIES_WITH_NULL_PID, ownerClassType);
		}
		return toReturnCastedObject;
	}
}