package tr.com.poc.temporaldate.core.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.common.base.Predicates;

import lombok.extern.log4j.Log4j2;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import tr.com.poc.temporaldate.common.Constants;

/**
 * Separated Swagger configuration from {@link ApplicationConfiguration} since Swagger Configuration will directly depend upon the spring 
 * profile chosen while building the package
 * 
 * @author umutaskin
 *
 */
@Configuration
@EnableSwagger2
@Profile(value= {Constants.PROFILE_DEV, Constants.PROFILE_QA, Constants.PROFILE_UAT})
@Log4j2
public class SwaggerConfiguration 
{
	@Bean
    public Docket api() 
	{
		log.debug("Enabling SWAGGER on current profile. Swagger link is: http://domain/swagger-ui.html");
        return new Docket(DocumentationType.SWAGGER_2)  
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
