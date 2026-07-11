package com.hms.events.security;

public class OtpExpiredByResendEvent
        extends SecurityEvent {

    public OtpExpiredByResendEvent(
            String username,
            String entityId) {

        super(username, "USER", entityId);
    }

    @Override
    public String action() {

        return "OTP_EXPIRED_BY_RESEND";
    }

    @Override
    public String details() {

        return "Previous OTP expired because a new OTP was generated";
    }

}