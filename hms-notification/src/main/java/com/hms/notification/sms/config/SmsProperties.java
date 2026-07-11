package com.hms.notification.sms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "hms.sms")
public class SmsProperties {

    /**
     * Enable SMS.
     */
    private boolean enabled = false;

    /**
     * Sender ID.
     */
    private String sender = "HMS";

}