package com.hms.identity.security.event;

public class RefreshTokenRevokedEvent
extends SecurityEvent {

public RefreshTokenRevokedEvent(
    String username,
    String entityId) {

super(username,"SESSION",entityId);
}

@Override
public String action() {

return "REFRESH_TOKEN_REVOKED";
}

@Override
public String details() {

return "Refresh token revoked";
}
}