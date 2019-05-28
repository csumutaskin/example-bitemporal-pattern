package tr.com.poc.temporaldate.core.configuration;

import static tr.com.poc.temporaldate.common.Constants.CLASSPATH_FOR_EXCEPTION_PROPERTIES;
import static tr.com.poc.temporaldate.common.Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS;
import static tr.com.poc.temporaldate.common.Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS;
import static tr.com.poc.temporaldate.common.Constants.UTF8;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityListeners;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.core.aspect.AspectBusinessValidationExceptionChecker;

/**
 * Contains Configuration beans on DB connection and other 3rd party tools that are being used
 *  
 * @author umutaskin
 *
 */
@Configuration
//@EnableJpaRepositories(basePackages= Constants.SCAN_PATH_JPA_REPOSITORIES, repositoryBaseClass = BaseDaoImpl.class)
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EntityListeners(AuditingEntityListener.class)
@Log4j2
public class ApplicationConfiguration
{		
	@Value("${spring.profiles.active:NoProfileChosen}")
	private String activeProfile;
	
	/*
	@Bean
	public RestTemplate restTemplate() 
	{
	    RestTemplate restTemplate = new RestTemplate();
	    List<HttpMessageConverter<?>> converters = new ArrayList<>();
	    MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
	    converters.add(jsonMessageConverter);
	    restTemplate.setMessageConverters(converters);
	    return restTemplate;
	}
	*/
	
	@Bean
	public MessageSource applicationExceptionMessageSource() 
	{
	    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	    messageSource.setBasename(CLASSPATH_FOR_EXCEPTION_PROPERTIES + MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS);
	    messageSource.setFallbackToSystemLocale(false);
	    messageSource.setDefaultEncoding(UTF8);
	    messageSource.setCacheSeconds(0);
	    return messageSource;
	}
		
	@Bean
	public MessageSource businessExceptionMessageSource() 
	{
	    final ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
	    messageSource.setBasename(CLASSPATH_FOR_EXCEPTION_PROPERTIES + MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS);
	    messageSource.setFallbackToSystemLocale(false);
	    messageSource.setDefaultEncoding(UTF8);
	    messageSource.setCacheSeconds(0);
	    return messageSource;
	}
		
    @Bean
    public AspectBusinessValidationExceptionChecker getAspectBusinessValidationExceptionChecker()
    {
    	return new AspectBusinessValidationExceptionChecker();
    }
    
    @PostConstruct
    public void promptSystemInfoLog()
    {
    	log.info("****************************************************************************************");
      	log.info("Current profile is: {}",activeProfile);
    	log.info("****************************************************************************************");
    }

}
