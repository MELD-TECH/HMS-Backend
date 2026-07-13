package com.hms.identity.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hms.authentication")
public class AuthenticationProperties {

    /**
     * Pending authentication validity.
     */
    private long pendingAuthenticationMinutes = 5;
    
    private Integer maximumResends = 5;

    private Integer resendCooldownSeconds = 30;

}