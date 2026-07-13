package com.hms.events.security;

import com.hms.audit.security.enums.AuditAction;

public class MfaVerifiedEvent extends SecurityEvent {

    public MfaVerifiedEvent(
            String username,
            String entityId) {

    	super(username, "USER", entityId);
    }
    
    @Override
    public String action() {

        return AuditAction.MFA_VERIFIED.name();
    }

    @Override
    public String details() {

        return "MFA verification completed";
    }
}