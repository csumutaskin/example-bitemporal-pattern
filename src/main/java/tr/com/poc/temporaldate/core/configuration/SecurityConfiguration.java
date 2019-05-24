package tr.com.poc.temporaldate.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;
import org.springframework.security.web.session.SessionManagementFilter;

import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.core.filter.AuditLoggingFilter;
import tr.com.poc.temporaldate.core.filter.ThreadLocalCleanerFilter;

/**
 * Security Configuration, embeds Spring Security Module to the project.
 * This is also where different Web Filters are applied to different end points of the core module.
 * All Web / HTTP security related configuration is done here. 
 * 
 * @author umutaskin
 *
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
	/**
	 * For this static configuration, all applied filters are below (in order):
	 * 
	 * <ul>
	 * 	<li>{@link WebAsyncManagerIntegrationFilter}
	 *  <li>{@link SecurityContextPersistenceFilter}
	 *  <li>{@link HeaderWriterFilter}
	 *  <li>{@link CsrfFilter}
	 *  <li>{@link LogoutFilter}
	 *  <li>{@link AuditLoggingFilter} - Custom Filter
	 *  <li>{@link RequestCacheAwareFilter}
	 *  <li>{@link SecurityContextHolderAwareRequestFilter}
	 *  <li>{@link AnonymousAuthenticationFilter}
	 *  <li>{@link SessionManagementFilter}
	 *  <li>{@link ExceptionTranslationFilter}
	 * </ul>
	 * 
	 * @author umutaskin
	 */
	@Configuration
	@Order(1)
	public static class SecurityConfigurationWithAuditFilter extends WebSecurityConfigurerAdapter
	{
		@Override
		protected void configure(HttpSecurity http) throws Exception 
		{		
			http.addFilterBefore(new ThreadLocalCleanerFilter(), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new AuditLoggingFilter(), UsernamePasswordAuthenticationFilter.class);
		}
		
		/**
		 * Ignore Unnecessary URLs from web filters
		 */
		@Override
		public void configure(WebSecurity web) throws Exception 
		{
			web.ignoring().antMatchers(Constants.IGNORE_SECURITY_AT_SWAGGER_WEBJARS_URL)
				.antMatchers(Constants.IGNORE_SECURITY_AT_ERROR_URL)
				.antMatchers(Constants.IGNORE_SECURITY_AT_RESOURCES_URL)
				.antMatchers(Constants.IGNORE_SECURITY_AT_SWAGGER_URL)
				.antMatchers(Constants.IGNORE_SECURITY_AT_SWAGGER_API_DOCS_URL);
		}
	}
}
