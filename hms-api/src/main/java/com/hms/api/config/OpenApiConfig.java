package com.hms.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;

import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI hmsOpenApi() {

        return new OpenAPI()
                .info(
                        new Info()
                                .title(
                                        "Hospital Management System API"
                                )
                                .version("1.0.0")
                                .description(
                                        "Core HMS API"
                                )
                );
    }
}