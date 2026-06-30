package com.hms.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.hms.identity.password.config.PasswordPolicyProperties;

@Configuration
@EnableConfigurationProperties(
        PasswordPolicyProperties.class
)
public class PasswordConfig {
}
