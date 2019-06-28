package tr.com.poc.temporaldate.core.filter;

import static tr.com.poc.temporaldate.common.Constants.MDC_CLIENT_IP;
import static tr.com.poc.temporaldate.common.Constants.MDC_HOST_ADDRESS;
import static tr.com.poc.temporaldate.common.Constants.MDC_TRANSACTION_NO;
import static tr.com.poc.temporaldate.common.Constants.MDC_URI;
import static tr.com.poc.temporaldate.common.Constants.MDC_USERNAME;
import static tr.com.poc.temporaldate.common.Constants.REQUEST_HEADER_USERNAME;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.bitemporalexample.service.BitemporalOrganizationService;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.core.util.RandomGenerator;

/**
 * Filter implementation to map some important ThreadContext values for advanced/better logging.
 * 
 * Current map includes values for: <i>(PER USER THREAD)</i>
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
@Log4j2
public class AuditLoggingFilter extends GenericFilterBean
{
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		long start = System.currentTimeMillis();
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		try
		{
			ThreadContext.put(MDC_CLIENT_IP, getClientIP(httpRequest));
			ThreadContext.put(MDC_TRANSACTION_NO, RandomGenerator.getInstance().getRandomUUIDString());
			ThreadContext.put(MDC_USERNAME, getUserName(httpRequest));
			ThreadContext.put(MDC_URI, httpRequest.getRequestURI());
			ThreadContext.put(MDC_HOST_ADDRESS, InetAddress.getLocalHost().getHostAddress());			
			chain.doFilter(request, response);
		}
		finally
		{			
			long timeSpent = (System.currentTimeMillis() - start);
			log.info("REQUEST: {}, TIME SPENT: {} msec", ThreadContext.get(MDC_URI), timeSpent);
			emptyThreadLocalVariables();			
		}
	}

	private void emptyThreadLocalVariables()
	{
		ThreadContext.remove(MDC_CLIENT_IP);
		ThreadContext.remove(MDC_TRANSACTION_NO);
		ThreadContext.remove(MDC_USERNAME);
		ThreadContext.remove(MDC_URI);
		ThreadContext.remove(MDC_HOST_ADDRESS);		
	}	
	
	/* Tries to detect client IP using alternative ways, If none detected, returns NA */
	private String getClientIP(HttpServletRequest request)
	{
		String clientIP = request.getHeader("X-Forwarded-For");
		if (StringUtils.isEmpty(clientIP))
		{
			clientIP = request.getRemoteAddr();
		}
		if (StringUtils.isEmpty(clientIP))
		{
			clientIP = Constants.NA;
		}
		return clientIP;
	}
	
	/* Returns username from request header parameter username, if none detected, retursn NA */
	private String getUserName(HttpServletRequest request)
	{
		String userName = request.getHeader(REQUEST_HEADER_USERNAME);
		if (StringUtils.isEmpty(userName))
		{
			userName = Constants.NA;
		}
		return userName;
	}
}
