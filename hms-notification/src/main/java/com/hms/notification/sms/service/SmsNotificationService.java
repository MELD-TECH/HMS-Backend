package com.hms.notification.sms.service;

import org.springframework.stereotype.Service;

import com.hms.notification.provider.SmsProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsNotificationService {

    private final SmsProvider provider;

    public void sendOtp(

            String phoneNumber,

            String otp) {

        provider.send(

                phoneNumber,

                """

                Your HMS verification code is:

                %s

                This code expires in 5 minutes.

                """

                .formatted(otp));

    }

}