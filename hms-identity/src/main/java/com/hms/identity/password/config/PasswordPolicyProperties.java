package com.hms.identity.password.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(
        prefix = "security.password"
)
public class PasswordPolicyProperties {

    private int minimumLength = 12;

    private int maximumLength = 64;

    private boolean requireUppercase = true;

    private boolean requireLowercase = true;

    private boolean requireDigit = true;

    private boolean requireSpecialCharacter = true;

    private int historySize = 5;

    private int expiryDays = 90;
    
    private int historyCount = 5;

}