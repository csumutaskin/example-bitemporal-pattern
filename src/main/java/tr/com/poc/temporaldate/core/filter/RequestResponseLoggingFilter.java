package tr.com.poc.temporaldate.core.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.web.filter.GenericFilterBean;

/**
 * For database and text logging of requests and responses if necessary
 * @author umutaskin
 *
 */
public class RequestResponseLoggingFilter extends GenericFilterBean 
{
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		chain.doFilter(request, response);
	}
}
