package tr.com.poc.temporaldate.core.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * Filter implementation to clear all Request Thread Variables in necessary objects. 
 * Acts as a Request Cleaner filter for all reused threads of reusable Application Server Request Threads
 * 
 * Currently cleans
 * <ul>
 * <li> <b>CLIENT_IP:</b> IP from which the current request is requested
 * <li> <b>TRANSACTION_NO:</b> a unique request number to follow request logs easily 
 * <li> <b>USERNAME:</b> which user (username / "anonymous" if user not authenticated) is requesting a service/data
 * <li> <b>URI:</b> which URI is currently requested by the end user
 * <li> <b>HOST_ADDRESS:</b> Which server (IP or hostname) is responding the current request
 * </ul> 
 * @author umutaskin
 *
 */
public class ThreadLocalCleanerFilter extends GenericFilterBean 
{
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		chain.doFilter(request, response);
	}
}
