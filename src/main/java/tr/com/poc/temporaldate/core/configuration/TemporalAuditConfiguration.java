package tr.com.poc.temporaldate.core.configuration;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Interceptor for auditting data as CREATE_DATE, MODIFY_DATE and tuples' authors on entities
 *
 * @author umut
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditor")
public class TemporalAuditConfiguration 
{
    @Bean(name="auditor")
    public AuditorAware<String> getAuditor()
    {
        //SecurityContextHolder.getContext().getAuthentication()...
        return () -> Optional.ofNullable("Umut Askin");
    }
}