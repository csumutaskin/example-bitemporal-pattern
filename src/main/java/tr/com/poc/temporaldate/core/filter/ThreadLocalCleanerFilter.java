package tr.com.poc.temporaldate.core.filter;

import static tr.com.poc.temporaldate.common.Constants.MDC_CLIENT_IP;
import static tr.com.poc.temporaldate.common.Constants.MDC_HOST_ADDRESS;
import static tr.com.poc.temporaldate.common.Constants.MDC_TRANSACTION_NO;
import static tr.com.poc.temporaldate.common.Constants.MDC_URI;
import static tr.com.poc.temporaldate.common.Constants.MDC_USERNAME;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.filter.GenericFilterBean;

import tr.com.poc.temporaldate.core.util.request.BusinessValidationExceptionsHolder;

/**
 * Filter implementation to clear all Request Thread Variables in necessary objects. 
 * Acts as a Request Cleaner filter for all reused threads of reusable Application Server Request Threads
 * 
 * Currently cleans
 * <ul>
 * <li> <b>ThreadContext - CLIENT_IP:</b> IP from which the current request is requested
 * <li> <b>ThreadContext - TRANSACTION_NO:</b> a unique request number to follow request logs easily 
 * <li> <b>ThreadContext - USERNAME:</b> which user (username / "anonymous" if user not authenticated) is requesting a service/data
 * <li> <b>ThreadContext - URI:</b> which URI is currently requested by the end user
 * <li> <b>ThreadContext - HOST_ADDRESS:</b> Which server (IP or hostname) is responding the current request
 * <li>  <b>{@link BusinessValidationExceptionsHolder}</b> data
 * </ul> 
 * @author umutaskin
 *
 */
public class ThreadLocalCleanerFilter extends GenericFilterBean 
{
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		try
		{
			chain.doFilter(request, response);
		}
		finally
		{
			// Below variables were filled in AuditLoggingFilter
			ThreadContext.remove(MDC_CLIENT_IP);
			ThreadContext.remove(MDC_TRANSACTION_NO);
			ThreadContext.remove(MDC_USERNAME);
			ThreadContext.remove(MDC_URI);
			ThreadContext.remove(MDC_HOST_ADDRESS);	
			
			//Below might be filled throughout the request.
			BusinessValidationExceptionsHolder.getInstance().clean();
		}
	}
}
