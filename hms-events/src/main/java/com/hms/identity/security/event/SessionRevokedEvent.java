package com.hms.identity.security.event;

public class SessionRevokedEvent
extends SecurityEvent {

public SessionRevokedEvent(
    String username,
    String entityId) {

super(username,"SESSION",entityId);
}

@Override
public String action() {

return "SESSION_REVOKED";
}

@Override
public String details() {

return "Administrator revoked session";
}
}
