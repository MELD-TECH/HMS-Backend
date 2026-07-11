package com.hms.events.security;

public class OtpResendLimitExceededEvent
        extends SecurityEvent {

    public OtpResendLimitExceededEvent(
            String username,
            String entityId) {

        super(username, "USER", entityId);
    }

    @Override
    public String action() {

        return "OTP_RESEND_LIMIT_EXCEEDED";
    }

    @Override
    public String details() {

        return "OTP resend limit exceeded";
    }

}