package com.hms.notification.mfa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "hms.security.mfa")
@Getter
@Setter
public class MfaProperties {

    /**
     * Enable MFA globally.
     */
    private boolean enabled = true;

    /**
     * OTP validity.
     */
    private long otpExpiryMinutes = 5;

    /**
     * OTP length.
     */
    private int otpLength = 6;

    /**
     * Maximum verification attempts.
     */
    private int maxAttempts = 5;

    /**
     * Maximum resend count.
     */
    private int maxResend = 3;

    /**
     * Enable email OTP.
     */
    private boolean emailEnabled = true;

    /**
     * Enable SMS OTP.
     */
    private boolean smsEnabled = false;

    /*
     * Maximum resend requests
     * before a new login is required.
     */
    private Integer maxResends = 3;

    /*
     * Minimum delay between resend requests.
     */
    private Integer resendCooldownSeconds = 30;
}
