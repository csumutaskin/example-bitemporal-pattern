package tr.com.poc.temporaldate.core.configuration.profiling;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("qa")
@PropertySource("classpath:qa/application-qa.properties")
public class QAConfiguration 
{
	@PostConstruct
	public void printInfo()
	{
		
	}
}
