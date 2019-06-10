package tr.com.poc.temporaldate.core.configuration.profiling;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("uat")
@PropertySource("classpath:uat/application-uat.properties")
public class UATConfiguration 
{
	@PostConstruct
	public void printInfo()
	{
		
	}
}
