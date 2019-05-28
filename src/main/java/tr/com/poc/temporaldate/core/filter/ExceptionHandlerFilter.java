package tr.com.poc.temporaldate.core.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import tr.com.poc.temporaldate.core.exception.RestExceptionHandler;

/**
 * Handles all exceptions which are not caught by the {@link RestExceptionHandler}
 * 
 * @author umutaskin
 *
 */
public class ExceptionHandlerFilter extends GenericFilterBean 
{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		chain.doFilter(request, response);
	}

}
