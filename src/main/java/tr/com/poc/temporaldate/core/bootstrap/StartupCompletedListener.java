package tr.com.poc.temporaldate.core.bootstrap;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Sets;

import lombok.extern.log4j.Log4j2;
import tr.com.poc.temporaldate.common.Constants;
import tr.com.poc.temporaldate.common.ExceptionConstants;
import tr.com.poc.temporaldate.core.dao.annotation.PidDetector;
import tr.com.poc.temporaldate.core.exception.ApplicationException;
import tr.com.poc.temporaldate.core.util.logging.RestServerLogInit;

/**
 * After all spring beans are initialized onApplicationEvent run once,
 * Use this class, if anyone needs to do a startup check and want to use the power of the spring context also. 
 * (Beans are already initialized here) 
 * 
 * @author umutaskin
 *
 */
@Component
@Log4j2
public class StartupCompletedListener implements ApplicationListener<ContextRefreshedEvent> 
{
	public static final String LOG_SUFFIX = "LOG";
	public static final String GUI_SUFFIX = "GUI";
	public static final String CLASSPATH_PREFIX = "classpath:";
	
	private static final String INTER_LOG_MESSAGE_1 = " bundle's i18n keys differ: One file contains keys: ";
	private static final String INTER_LOG_MESSAGE_2 = " that the other does not contain";
	private static final String INTER_LOG_MESSAGE_3 = ", and ";
	private static final String INTER_LOG_MESSAGE_4 = " vice versa.";

    @Value("#{servletContext.contextPath}")
    public String contextPathUrl;
	
	@Autowired
	private ResourceLoader rl;
	
	/**
	 * Runs a series of startup controls in order to decrease erroneous situations while the server is up.
	 * @param event Context Refreshed Event to represent spring context for the application
	 */
    @Override 
    public void onApplicationEvent(ContextRefreshedEvent event) 
    {
        log.info("Spring Context is up and all beans are initialized now...");
        
        PidDetector.detectPidAnnotations();
        RestServerLogInit.init(contextPathUrl);
//      TODO:  EntityDetector detectEntityAssociations()

        checkBusinessExceptionsI18NKeysAreSynchronizedOrNot();
        checkApplicationExceptionsI18NKeysAreSynchronizedOrNot();
        log.info("All startup checks are done, no important issue found to cancel build/startup process...");
    }
    
    /*
     * Checks whether all keys in each language file of business-exception.properties exist in all files, throws an exception and logs an error if not.
     */
    private void checkBusinessExceptionsI18NKeysAreSynchronizedOrNot() 
    {
        Set<Object> busKeys = this.getPropertySetOfaBundle(Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS_DEFAULT_PATH);
        Set<Object> busEnKeys = this.getPropertySetOfaBundle(Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS_EN_PATH);
        checkInternationalizedKeysAreComplete(busKeys, busEnKeys, Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS_DEFAULT_PATH, GUI_SUFFIX, true);
        checkInternationalizedKeysAreComplete(busKeys, busEnKeys, Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_BUSINESS_EXCEPTIONS_DEFAULT_PATH, LOG_SUFFIX, false);
    }
    
    /*
     * Checks whether all keys in each language file of application-exception.properties exist in all files, throws an exception and logs an error if not.
     */
    private void checkApplicationExceptionsI18NKeysAreSynchronizedOrNot() 
    {
        Set<Object> appKeys = this.getPropertySetOfaBundle(Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS_DEFAULT_PATH);
        Set<Object> appEnKeys = this.getPropertySetOfaBundle(Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS_EN_PATH);
      	checkInternationalizedKeysAreComplete(appKeys, appEnKeys, Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS_DEFAULT_PATH, GUI_SUFFIX, true);
        checkInternationalizedKeysAreComplete(appKeys, appEnKeys, Constants.MESSAGE_BUNDLE_FILE_NAME_FOR_APPLICATION_EXCEPTIONS_DEFAULT_PATH, LOG_SUFFIX, false); 
    }
    
    //Returns the property set of a bundle represented by its classpath
    private Set<Object> getPropertySetOfaBundle(String path)
    {
    	if(StringUtils.isBlank(path))
    	{
    		return new HashSet<>();
    	}
    	String classpathPrefixedPath = CLASSPATH_PREFIX + path;
    	Resource resource = rl.getResource(classpathPrefixedPath);	
    	try
    	{
    		return PropertiesLoaderUtils.loadProperties(resource).keySet();
    	}
    	catch(IOException e)
    	{
    		log.error("Error checking all the properties of the bundle: {}. Check whether the file exists on the given path to fix the problem", classpathPrefixedPath);
    		throw new ApplicationException(ExceptionConstants.SERVER_STARTUP_EXCEPTION);
    	}
    }    
    
    /*
     * Compares different language bundles and checks whether there are problems
     * toCompareSet1: Set 1 to be compared and checked for different tuples that toCompareSet2 does not contain
     * toCompareSet1: Set 2 to be compared and checked for different tuples that toCompareSet1 does not contain
     * pathOfFile: in case of differences are detected, path of file indicates which files should be corrected and synchronized with other
     * suffix: can be GUI or LOG. this is to group GUI logs and LOG logs for a compact comma separated message
     * throwAndLogError: if true error log is written to the file/console and an exception is thrown, if false warning log is written to the file/console
     */
    private void checkInternationalizedKeysAreComplete(Set<Object> toCompareSet1, Set<Object> toCompareSet2, String pathOfFile, String suffix, boolean throwAndLogError)
    {
    	Set<Object> differenceOf1 = Sets.difference(toCompareSet1, toCompareSet2);
    	Set<Object> differenceOf2 = Sets.difference(toCompareSet2, toCompareSet1);
    	
    	StringBuilder message = new StringBuilder(); 
    	
    	if(!CollectionUtils.isEmpty(differenceOf1))
    	{
    		String dif1Message = differenceOf1.stream().filter(e -> e != null && e.toString().endsWith(suffix)).map(Object::toString).collect(Collectors.joining(", "));
    		if(StringUtils.isNoneBlank(dif1Message))
    		{
    			message.append(pathOfFile).append(INTER_LOG_MESSAGE_1).append(dif1Message).append(INTER_LOG_MESSAGE_2);
    		}
    	}
    	if(!CollectionUtils.isEmpty(differenceOf2))
    	{
    		String dif2Message = differenceOf2.stream().filter(e -> e != null && e.toString().endsWith(suffix)).map(Object::toString).collect(Collectors.joining(", "));
    		if(StringUtils.isNoneBlank(dif2Message))
    		{
    			if(message.length() == 0)
    			{	
    				message.append(pathOfFile).append(INTER_LOG_MESSAGE_1).append(dif2Message).append(INTER_LOG_MESSAGE_2);
    			}
    			else
    			{
    				message.append(INTER_LOG_MESSAGE_3).append(dif2Message).append(INTER_LOG_MESSAGE_4);
    			}
    		}
    	}
    	if(message.length() > 0)
    	{
    		if(throwAndLogError)
    		{
    			log.error(message.toString());
    			throw new ApplicationException(ExceptionConstants.SERVER_STARTUP_EXCEPTION);
    		}
    		log.warn(message.toString());
    	}
    }

}