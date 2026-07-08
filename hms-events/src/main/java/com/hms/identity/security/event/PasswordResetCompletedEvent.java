package com.hms.identity.security.event;

public class PasswordResetCompletedEvent
extends SecurityEvent {

public PasswordResetCompletedEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return "PASSWORD_RESET";
}

@Override
public String details() {

return "Password reset completed";
}
}
