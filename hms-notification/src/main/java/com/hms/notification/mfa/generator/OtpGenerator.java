package com.hms.notification.mfa.generator;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

import com.hms.notification.mfa.config.MfaProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpGenerator {

    private final SecureRandom random =
            new SecureRandom();

    private final MfaProperties properties;

    public String generate() {

        int length =
                properties.getOtpLength();

        StringBuilder builder =
                new StringBuilder();

        for (int i = 0; i < length; i++) {

            builder.append(

                    random.nextInt(10));

        }

        return builder.toString();

    }

}
