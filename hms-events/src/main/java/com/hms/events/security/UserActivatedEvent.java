package com.hms.events.security;

public class UserActivatedEvent
extends SecurityEvent {

public UserActivatedEvent(
    String username,
    String entityId) {

super(username,"USER",entityId);
}

@Override
public String action() {

return "USER_ACTIVATED";
}

@Override
public String details() {

return "User activated";
}
}