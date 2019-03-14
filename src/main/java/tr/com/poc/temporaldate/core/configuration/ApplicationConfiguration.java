package tr.com.poc.temporaldate.core.configuration;

import javax.persistence.EntityListeners;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableJpaAuditing
//@EnableJpaRepositories(basePackages= Constants.SCAN_PATH_JPA_REPOSITORIES, repositoryBaseClass = BaseDaoImpl.class)
@EnableTransactionManagement
@EntityListeners(AuditingEntityListener.class)
@EnableSwagger2
//@Log4j2
public class ApplicationConfiguration 
{	
	@Bean
    public Docket api() 
	{ 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.any())              
          .paths(PathSelectors.any())                          
          .build();                                           
    }
}
