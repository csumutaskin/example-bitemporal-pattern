package tr.com.poc.temporaldate.core.configuration.profiling;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import tr.com.poc.temporaldate.common.Constants;

@Configuration
@Profile(Constants.PROFILE_UAT)
@PropertySource("classpath:uat/application-uat.properties")
public class UATConfiguration 
{
	@PostConstruct
	public void printInfo()
	{
		//In case of UAT specific build is necessary for the project, this method can be used to differ environment specific logging
	}
}
