package com.hms.events.security;

import com.hms.audit.security.enums.AuditAction;

public class RoleAssignedEvent
extends SecurityEvent {

public RoleAssignedEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return AuditAction.ROLE_ASSIGNED.name();
}

@Override
public String details() {

return "Role assigned";
}
}
