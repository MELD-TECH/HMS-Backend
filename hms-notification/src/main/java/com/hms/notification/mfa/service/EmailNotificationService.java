package com.hms.notification.mfa.service;

import org.springframework.stereotype.Service;

import com.hms.notification.mfa.config.MfaProperties;
import com.hms.notification.provider.EmailProvider;
import com.hms.notification.template.OtpEmailTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final EmailProvider provider;

    private final OtpEmailTemplate template;

    private final MfaProperties properties;

    public void sendOtp(
            String email,
            String otp) {

        provider.send(

                email,

                "HMS Verification Code",

                template.build(

                        otp,

                        properties.getOtpExpiryMinutes()));

    }

}