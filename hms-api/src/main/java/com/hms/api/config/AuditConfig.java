package com.hms.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Configuration
@EnableAspectJAutoProxy
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorAware() {

        return () -> Optional.of("SYSTEM");
    }
}