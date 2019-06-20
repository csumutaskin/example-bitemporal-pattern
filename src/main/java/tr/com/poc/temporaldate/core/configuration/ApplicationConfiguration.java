package tr.com.poc.temporaldate.core.configuration;

import static tr.com.poc.temporaldate.common.Constants.CLASSPATH_FOR_EXCEPTION_PROPERTIES;
import static tr.com.poc.temporaldate.common.Constants.MDC_CLIENT_IP;
import static tr.com.poc.temporaldate.common.Constants.MDC_HOST_ADDRESS;
import static tr.com.poc.temporaldate.common.Constants.MDC_TRANSACTION_NO;
import static tr.com.poc.temporaldate.common.Constants.MDC_URI;
import static tr.com.poc.temporaldate.common.Constants.MDC_USERNAME;
import static tr.com.poc.temporaldate.common.Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS;
import static tr.com.poc.temporaldate.common.Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS;
import static tr.com.poc.temporaldate.common.Constants.STARTUP;
import static tr.com.poc.temporaldate.common.Constants.UTF8;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.EntityListeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LoggerContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
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
	private List<String> validEnvironments = Stream.of(Constants.PROFILE_DEV, Constants.PROFILE_QA,Constants.PROFILE_UAT,Constants.PROFILE_PREPROD,Constants.PROFILE_PROD).collect(Collectors.toList());
	
	@Value("${spring.profiles.active:NoProfileChosen}")
	private String activeProfile;
	
	@Value("${common.log.pattern:[!CommonLogPatternRetrieveProblemDevProfile]%msg%n}")
	private String commonLogPattern;
	
	@Value("${management.server.port:can_not_resolve}")
	private String actuatorPort;

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
    public void promptSystemInfoLog() throws IOException
    {       	
    	setMDCDefaults();
    	
    	System.setProperty("common.log.pattern", commonLogPattern);
    	log.info("****************************************************************************************");
    	if(isValidEnvironment(activeProfile))
    	{
    		LoggerContext context = (LoggerContext) LogManager.getContext(false);
    		File file = new ClassPathResource(activeProfile + "/log4j2.xml").getFile(); 
    		context.setConfigLocation(file.toURI());
    		log.info("Current profile is: {}",activeProfile);      
    	}
    	else
    	{
    		log.warn("Current profile is: {}, This is NOT a predefined environment, some functions may not function properly if the profiling is not correct...", activeProfile);
    		log.info("Valid predefined environment set: {}, current profile is: {}", validEnvironments, activeProfile);
    	}      	
      	log.info("Actuator port is: {}",actuatorPort);
    	log.info("****************************************************************************************");
    }
    
    //Checks whether current profile is a known profile
    private boolean isValidEnvironment(String activeProfile)
    {
    	boolean toReturn = false;
    	if(validEnvironments.contains(activeProfile))
    	{
    		toReturn = true;
    	}
    	return toReturn;
    }
    
    //Sets ThreadContextMap Default values
    private void setMDCDefaults() throws IOException
    {
    	ThreadContext.put(MDC_CLIENT_IP, STARTUP);
		ThreadContext.put(MDC_TRANSACTION_NO, STARTUP);
		ThreadContext.put(MDC_USERNAME, STARTUP);
		ThreadContext.put(MDC_URI, STARTUP);
		ThreadContext.put(MDC_HOST_ADDRESS, InetAddress.getLocalHost().getHostAddress());		
    }
}
