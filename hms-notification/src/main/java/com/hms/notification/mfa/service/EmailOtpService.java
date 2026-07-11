package com.hms.notification.mfa.service;

import org.springframework.stereotype.Service;

import com.hms.notification.dto.GenerateOtpRequest;
import com.hms.notification.dto.OtpResponse;
import com.hms.notification.dto.ResendOtpRequest;
import com.hms.notification.mfa.entity.OtpCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailOtpService {

    private final OtpService otpService;

    private final EmailNotificationService emailService;

    public OtpResponse generate(

            GenerateOtpRequest request) {

        OtpCode otp =

                otpService.generate(request);

        emailService.sendOtp(

                request.recipient(),

                otp.getCode());

        return OtpResponse.builder()

                .otpId(otp.getId())

                .expiresAt(otp.getExpiresAt())

                .build();

    }
    
    public OtpResponse resend(

            ResendOtpRequest request) {

        OtpCode otp =

                otpService.resend(request);

        emailService.sendOtp(

                request.recipient(),

                otp.getCode());

        return OtpResponse.builder()

                .otpId(otp.getId())

                .expiresAt(otp.getExpiresAt())

                .build();

    }

}
