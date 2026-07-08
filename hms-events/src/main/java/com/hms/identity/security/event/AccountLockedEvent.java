package com.hms.identity.security.event;

public class AccountLockedEvent
extends SecurityEvent {

public AccountLockedEvent(
    String username,
    String entityId) {

super(username, "USER", entityId);
}

@Override
public String action() {
return "ACCOUNT_LOCKED";
}

@Override
public String details() {
return "Account locked after maximum failed logins";
}
}