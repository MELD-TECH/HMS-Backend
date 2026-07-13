package com.hms.events.security;

import com.hms.audit.security.enums.AuditAction;

public class MfaAuthenticationCompletedEvent
        extends SecurityEvent {

    public MfaAuthenticationCompletedEvent(

            String username,

            String entityId) {

        super(

                username,

                "USER",

                entityId);
    }

    @Override
    public String action() {

        return AuditAction.MFA_AUTHENTICATION_COMPLETED.name();
    }

    @Override
    public String details() {

        return "Multi-factor authentication completed successfully.";
    }
}