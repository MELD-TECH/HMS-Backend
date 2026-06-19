package com.hms.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;



@SpringBootApplication(
        scanBasePackages = {
                "com.hms"
        }
)
@EnableSpringDataWebSupport(pageSerializationMode = PageSerializationMode.VIA_DTO)
@EnableJpaRepositories(basePackages = { "com.hms.*"})
@EntityScan(basePackages = {"com.hms.*"})
@EnableJpaAuditing
@EnableMethodSecurity
public class HmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                HmsApplication.class,
                args
        );
    }
    
}