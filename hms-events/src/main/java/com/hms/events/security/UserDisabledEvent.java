package com.hms.events.security;

public class UserDisabledEvent
extends SecurityEvent {

public UserDisabledEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return "USER_DISABLED";
}

@Override
public String details() {

return "User disabled";
}
}