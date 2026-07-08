package com.hms.identity.security.event;

public class RefreshTokenCreatedEvent
extends SecurityEvent {

public RefreshTokenCreatedEvent(
    String username,
    String entityId) {

super(username,"SESSION",entityId);
}

@Override
public String action() {

return "REFRESH_TOKEN_CREATED";
}

@Override
public String details() {

return "Refresh token created";
}
}