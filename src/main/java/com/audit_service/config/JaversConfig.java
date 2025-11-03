package com.audit_service.config;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JaVers configuration for object comparison only (no database persistence)
 */
@Configuration
public class JaversConfig {

    @Bean
    public Javers javers() {
        return JaversBuilder.javers().build();
    }
}

