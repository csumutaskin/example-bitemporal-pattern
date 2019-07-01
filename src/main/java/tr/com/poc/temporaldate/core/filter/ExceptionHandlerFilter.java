package tr.com.poc.temporaldate.core.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.filter.GenericFilterBean;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.exception.RestExceptionHandler;

/**
 * Handles all exceptions which are not caught by the {@link RestExceptionHandler}
 * 
 * @author umutaskin
 *
 */
@Log4j2
public class ExceptionHandlerFilter extends GenericFilterBean 
{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		try
		{
			chain.doFilter(request, response);
		}
		catch(Exception e)
		{
			//TODO: Convert to custom output format for unexpectedly thrown exceptions
			log.error(ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}
}
