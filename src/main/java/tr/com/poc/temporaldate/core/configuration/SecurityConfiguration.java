package tr.com.poc.temporaldate.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import tr.com.poc.temporaldate.core.filter.AuditLoggingFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	@Override
	protected void configure(HttpSecurity http) throws Exception 
	{		
		http.addFilterBefore(auditLoggingFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	/**
	 * Ignore Unnecessary URLs from web filters
	 */
	@Override
	public void configure(WebSecurity web) throws Exception 
	{
		/*
	    web.ignoring().antMatchers(Constants.IGNORE_SECURITY_AT_SWAGGER_WEBJARS_URL)
	    			  .antMatchers(Constants.IGNORE_SECURITY_AT_SWAGGER_CORS_URL)
	    			  .antMatchers(Constants.IGNORE_SECURITY_AT_ERROR_URL);
	    			  */
		
		web.ignoring().antMatchers("/temporal/**")
		.antMatchers("temporal/**")
		.antMatchers("/**")
		.antMatchers("**")
		;
		
	}
	
	@Bean
	public AuditLoggingFilter auditLoggingFilter()
	{
		return new AuditLoggingFilter();
	}
}
