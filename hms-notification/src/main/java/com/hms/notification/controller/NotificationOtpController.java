package com.hms.notification.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.notification.dto.GenerateOtpRequest;
import com.hms.notification.dto.OtpResponse;
import com.hms.notification.dto.ResendOtpRequest;
import com.hms.notification.dto.VerifyOtpRequest;
import com.hms.notification.mfa.service.EmailOtpService;
import com.hms.notification.mfa.service.OtpService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/notifications/otp")
@RequiredArgsConstructor
public class NotificationOtpController {

    private final EmailOtpService emailOtpService;
    
    private final OtpService otpService;

    @PostMapping("/email")

    public ResponseEntity<OtpResponse> generate(

            @RequestBody
            GenerateOtpRequest request) {

        return ResponseEntity.ok(

                emailOtpService.generate(request));

    }
    
    @PostMapping("/verify")
    public ResponseEntity<Void> verify(@RequestBody VerifyOtpRequest request){

    otpService.verify(request);

    return ResponseEntity.ok().build();

    }
    
    @PostMapping("/resend")

    public ResponseEntity<Void> resend(

            @RequestBody
            @Valid
            ResendOtpRequest request) {

    	otpService.resend(request);

        return ResponseEntity.noContent()

                .build();

    }

}
