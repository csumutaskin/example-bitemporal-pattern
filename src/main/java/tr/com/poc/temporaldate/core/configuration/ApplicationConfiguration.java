package tr.com.poc.temporaldate.core.configuration;

import java.util.Collections;

import javax.persistence.EntityListeners;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.google.common.base.Predicates;

import lombok.extern.log4j.Log4j2;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Contains Configuration beans on DB connection and other 3rd party tools that are being used
 *  
 * @author umutaskin
 *
 */
@Configuration
//@EnableJpaRepositories(basePackages= Constants.SCAN_PATH_JPA_REPOSITORIES, repositoryBaseClass = BaseDaoImpl.class)
@EnableTransactionManagement
@EntityListeners(AuditingEntityListener.class)
@EnableSwagger2
@Log4j2
public class ApplicationConfiguration
{	
	@Bean
    public Docket api() 
	{
		log.debug("Creating docket for SWAGGER_2");
        return new Docket(DocumentationType.SWAGGER_2)  
        		.host("http://localhost:8080")
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())          
          .paths(Predicates.not(PathSelectors.regex("/error.*")))
          .build().apiInfo(apiInformation());                                        
    }
	
	private ApiInfo apiInformation() 
	{
	    return new ApiInfo("TEMPORAL BITEMPORAL DATE PATTERN SHOWCASE REST API", "This API contains methods that facilitate how temporal and bitemporal patterns can be implemented in a simple boot application.", "API TOS", "Terms of service", 
	    		new Contact("Umut Askin", "NA", "umutaskin@gmail.com"), "License of API", "https://www.apache.org/licenses/LICENSE-2.0.txt", Collections.emptyList());
	}
}
