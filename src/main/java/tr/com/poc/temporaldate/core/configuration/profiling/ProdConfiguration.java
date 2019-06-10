package tr.com.poc.temporaldate.core.configuration.profiling;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod")
@PropertySource("classpath:prod/application-prod.properties")
public class ProdConfiguration 
{
	@PostConstruct
	public void printInfo()
	{
		
	}
}
