package tr.com.poc.temporaldate.core.configuration.profiling;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import tr.com.poc.temporaldate.common.Constants;

/**
 * Preprod specific bean for setting up server startup
 * 
 * @author umutaskin
 */
@Configuration
@Profile(Constants.PROFILE_PREPROD)
@PropertySource("classpath:preprod/application-preprod.properties")
public class PreprodConfiguration 
{
	@PostConstruct
	public void configureEnvironment()
	{
		//In case of pre-production specific build is necessary for the project, this method can be used to differ environment specific logging
	}
}
