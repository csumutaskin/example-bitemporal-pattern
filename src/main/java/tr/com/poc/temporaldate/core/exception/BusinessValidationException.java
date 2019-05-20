package tr.com.poc.temporaldate.core.exception;

import java.util.ArrayDeque;
import java.util.Deque;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;

/**
 * Validation exception
 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter 
@Setter 
@ToString 
@Log4j2
public class BusinessValidationException extends BusinessException 
{
	private static final String VALIDATION_EXCEPTION_CODE = ""; 
			
	private Deque<BusinessValidationExceptionItem> businessValidationExceptionItemList;
	
	public BusinessValidationException()
	{
		super(VALIDATION_EXCEPTION_CODE);
	}
	
	public void addBusinessValidationItem(BusinessValidationExceptionItem item)
	{
		if(item == null)
		{
			return;
		}
		if(businessValidationExceptionItemList == null)
		{
			businessValidationExceptionItemList = new ArrayDeque<>();
		}
		businessValidationExceptionItemList.add(item);
		log.debug("A businessvalidationitem is added to a business exception with exceptionItemCode: {} and exceptionItemMessage: {} ",item.getExceptionItemCode(), item.getExceptionItemMessage());
	}
	
	public void deleteBusinessValidationItemList()
	{
		businessValidationExceptionItemList =  new ArrayDeque<>();
		log.debug("All BusinessValidationItems in a BusinessValidationException are deleted");
	}
	
	public void addBusinessValidationItem(String exceptionCode)
	{
		
	}
}
