package com.hms.identity.security.event;

public class PermissionAssignedEvent
extends SecurityEvent {

public PermissionAssignedEvent(
    String username,
    String entityId) {

super(username,"ROLE",entityId);
}

@Override
public String action() {

return "PERMISSION_ASSIGNED";
}

@Override
public String details() {

return "Permission assigned";
}
}
