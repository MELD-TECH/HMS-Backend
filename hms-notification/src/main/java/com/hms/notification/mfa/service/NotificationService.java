package com.hms.notification.mfa.service;

public interface NotificationService {

    void sendOtp(
            String recipient,
            String otp);

}