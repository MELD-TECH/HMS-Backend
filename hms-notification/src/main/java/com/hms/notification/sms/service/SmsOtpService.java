package com.hms.notification.sms.service;

import org.springframework.stereotype.Service;

import com.hms.notification.dto.GenerateOtpRequest;
import com.hms.notification.dto.OtpResponse;
import com.hms.notification.dto.ResendOtpRequest;
import com.hms.notification.dto.VerifyOtpRequest;
import com.hms.notification.mfa.entity.OtpCode;
import com.hms.notification.mfa.service.OtpService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsOtpService {

    private final OtpService otpService;

    private final SmsNotificationService smsService;

    public OtpResponse generate(

            GenerateOtpRequest request) {

        OtpCode otp =

                otpService.generate(request);

        smsService.sendOtp(

                request.recipient(),

                otp.getCode());

        return OtpResponse.builder()

                .otpId(otp.getId())

                .expiresAt(otp.getExpiresAt())

                .build();

    }
    
    public void verify(
            VerifyOtpRequest request) {

        otpService.verify(request);

    }
    
    public OtpResponse resend(

            ResendOtpRequest request) {

        OtpCode otp =

                otpService.resend(request);

        smsService.sendOtp(

                request.recipient(),

                otp.getCode());

        return OtpResponse.builder()

                .otpId(otp.getId())

                .expiresAt(otp.getExpiresAt())

                .build();

    }
    

}