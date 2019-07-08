package tr.com.poc.temporaldate.core.configuration.profiling;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import tr.com.poc.temporaldate.common.Constants;

/**
 * Development specific bean for setting up server startup
 * 
 * @author umutaskin
 */
@Configuration
@Profile(Constants.PROFILE_DEV)
@PropertySource("classpath:dev/application-dev.properties")
public class DevConfiguration 
{
	@PostConstruct
	public void configureEnvironment() throws IOException
	{
		//In case of dev specific build is necessary for the project, this method can be used to differ environment specific logging
	}
}
