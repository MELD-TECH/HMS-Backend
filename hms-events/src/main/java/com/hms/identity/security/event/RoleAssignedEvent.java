package com.hms.identity.security.event;

import com.hms.identity.audit.enums.AuditAction;

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
