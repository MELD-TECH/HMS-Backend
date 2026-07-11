package com.hms.events.security;

public class SessionRevokedAllEvent
extends SecurityEvent {

public SessionRevokedAllEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return "ALL_SESSIONS_REVOKED";
}

@Override
public String details() {

return "All active sessions revoked";
}
}
