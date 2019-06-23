package tr.com.poc.temporaldate.core.exception;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.util.request.BusinessValidationExceptionsHolder;

/**
 * Validation exception, thrown in case of erroneous input data by the end user for a particular service request
 * 
 * @author umutaskin
 */
@SuppressWarnings("serial")
@Getter 
@Setter 
@ToString 
@Log4j2
public class BusinessValidationException extends BaseException 
{
	private static final String VALIDATION_EXCEPTION_CODE = ""; 
			
	private Deque<BusinessValidationExceptionItem> businessValidationExceptionItemList;
	
	public BusinessValidationException(String exceptionItemCode, String... exceptionMessageItemParameters)
	{
		super(VALIDATION_EXCEPTION_CODE);
		this.addBusinessValidationItem(exceptionItemCode, exceptionMessageItemParameters);
	}
	
	public BusinessValidationException(BusinessValidationExceptionItem item)
	{
		super(VALIDATION_EXCEPTION_CODE);
		this.addBusinessValidationItem(item);
	}
	
	public BusinessValidationException(Deque<BusinessValidationExceptionItem> businessValidationExceptionItemList) 
	{
		super(VALIDATION_EXCEPTION_CODE);
		this.businessValidationExceptionItemList = businessValidationExceptionItemList;
	}
		
	public void throwFinally() 
	{
		BusinessValidationExceptionsHolder.getInstance().addValidationException(this);
	}
	
	private void addBusinessValidationItem(BusinessValidationExceptionItem item)
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

	private void addBusinessValidationItem(String exceptionItemCode, String... exceptionMessageItemParameters)
	{
		if(StringUtils.isBlank(exceptionItemCode))
		{
			return;
		}
		BusinessValidationExceptionItem item = new BusinessValidationExceptionItem(exceptionItemCode, exceptionMessageItemParameters);
		addBusinessValidationItem(item);		
		log.debug("A businessvalidationitem is added to a business exception with exceptionItemCode: {} and exceptionItemMessage: {} ",item.getExceptionItemCode(), item.getExceptionItemMessage());
	}
}
