package com.hms.identity.security.event;

public class PasswordChangedEvent
extends SecurityEvent {

public PasswordChangedEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return "PASSWORD_CHANGED";
}

@Override
public String details() {

return "Password changed successfully";
}
}