package tr.com.poc.temporaldate.core.util.request;

import java.util.ArrayDeque;
import java.util.Deque;

import org.springframework.util.CollectionUtils;

import tr.com.poc.temporaldate.core.exception.BusinessValidationException;
import tr.com.poc.temporaldate.core.exception.BusinessValidationExceptionItem;

/**
 * Holds request specific data in thread local variables, and uses whenever necessary in that particular request
 * Data is always cleaned up in a specific web filter whenever data holding mission is no longer relevant for that request
 * e.g. Multiple exception items are held here
 * 
 * @author umutaskin
 *
 */
public class RequestThreadLocalHolder 
{
	private ThreadLocal<Deque<BusinessValidationExceptionItem>> businessValidationExceptionItems = new ThreadLocal<>();
	
	private RequestThreadLocalHolder()
	{}

	public static final RequestThreadLocalHolder getInstance() 
	{
		return RequestThreadLocalHolderHolder.INSTANCE;
	}
	
	public void addExceptionItem(BusinessValidationExceptionItem toAdd) 
	{
		Deque<BusinessValidationExceptionItem> deque = businessValidationExceptionItems.get();
		if(deque == null)
		{
			deque = new ArrayDeque<>();
		}
		deque.add(toAdd);		
		businessValidationExceptionItems.set(deque);
	}
	
	public void addValidationException(BusinessValidationException toAdd)
	{
		if(toAdd == null || CollectionUtils.isEmpty(toAdd.getBusinessValidationExceptionItemList()))
		{
			return;
		}
		Deque<BusinessValidationExceptionItem> temp = toAdd.getBusinessValidationExceptionItemList();
		for(BusinessValidationExceptionItem item : temp)
		{
			addExceptionItem(item);
		}
	}
		
	public void clean() 
	{
		businessValidationExceptionItems.remove();
		businessValidationExceptionItems = null;
	}
	
	private static class RequestThreadLocalHolderHolder
	{
		private static final RequestThreadLocalHolder INSTANCE = new RequestThreadLocalHolder();
	}
}

