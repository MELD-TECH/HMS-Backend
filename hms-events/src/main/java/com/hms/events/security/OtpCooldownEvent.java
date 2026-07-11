package com.hms.events.security;

public class OtpCooldownEvent
        extends SecurityEvent {

    public OtpCooldownEvent(
            String username,
            String entityId) {

        super(username, "USER", entityId);
    }

    @Override
    public String action() {

        return "OTP_RESEND_COOLDOWN";
    }

    @Override
    public String details() {

        return "OTP resend cooldown violation";
    }

}