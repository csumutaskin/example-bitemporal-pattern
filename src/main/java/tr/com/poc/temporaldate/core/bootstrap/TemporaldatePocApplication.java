package tr.com.poc.temporaldate.core.bootstrap;

import static tr.com.poc.temporaldate.common.Constants.SCAN_PATH_JPA_ENTITIES;
import static tr.com.poc.temporaldate.common.Constants.SCAN_PATH_SPRING_COMPONENTS;

import java.io.PrintStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Bootstrap class for boot application
 * @author umutaskin
 *
 */
@SpringBootApplication
@ComponentScan(SCAN_PATH_SPRING_COMPONENTS)
@EntityScan( basePackages = {SCAN_PATH_JPA_ENTITIES} )
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class TemporaldatePocApplication
{
	public static void main(String[] args) 
	{
		//System.setProperty("spring.banner.location", "classpath:dev/banner.txt");
		SpringApplication.run(TemporaldatePocApplication.class, args);
	}
}
