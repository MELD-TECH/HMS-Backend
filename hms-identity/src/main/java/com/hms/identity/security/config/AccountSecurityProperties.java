package com.hms.identity.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.account")
public class AccountSecurityProperties {

    private int maxFailedAttempts = 5;

    private int lockDurationMinutes = 30;

}