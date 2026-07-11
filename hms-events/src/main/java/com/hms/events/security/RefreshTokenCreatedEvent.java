package com.hms.events.security;

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