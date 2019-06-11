package tr.com.poc.temporaldate.core.configuration;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * For Web MVC support and to configure object converters that are to be used in Rest Services as different media types.
 * 
 * @author umutaskin
 *
 */
@Configuration
@EnableWebMvc
public class WebMvcConfiguration implements WebMvcConfigurer
{		
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) 
	{
		 configurer.defaultContentType(MediaType.APPLICATION_JSON);
	}
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) 
	{
        converters.add(jsonConverter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonConverter() 
    {
    	MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    	converter.setPrettyPrint(true);
        return converter;
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) 
    {
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
