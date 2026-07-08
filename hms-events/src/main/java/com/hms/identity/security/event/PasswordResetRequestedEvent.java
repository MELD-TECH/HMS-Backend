package com.hms.identity.security.event;

public class PasswordResetRequestedEvent
extends SecurityEvent {

public PasswordResetRequestedEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return "PASSWORD_RESET_REQUESTED";
}

@Override
public String details() {

return "Password reset requested";
}
}
