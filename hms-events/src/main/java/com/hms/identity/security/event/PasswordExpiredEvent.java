package com.hms.identity.security.event;


import com.hms.identity.audit.enums.AuditAction;

public class PasswordExpiredEvent
        extends SecurityEvent {

    public PasswordExpiredEvent(
            String username,
            String entityId) {

        super(
                username,
                "USER",
                entityId);

    }

    @Override
    public String action() {

        return AuditAction.PASSWORD_EXPIRED.name();

    }

    @Override
    public String details() {

        return "Password has expired.";

    }

}