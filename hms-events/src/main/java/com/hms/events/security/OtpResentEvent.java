package com.hms.events.security;

public class OtpResentEvent extends SecurityEvent {

    public OtpResentEvent(
            String username,
            String entityId) {

        super(username, "USER", entityId);
    }

    @Override
    public String action() {
        return "OTP_RESENT";
    }

    @Override
    public String details() {
        return "OTP resent";
    }
}