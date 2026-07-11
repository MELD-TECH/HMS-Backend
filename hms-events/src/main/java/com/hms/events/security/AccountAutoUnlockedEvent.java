package com.hms.events.security;

public class AccountAutoUnlockedEvent
extends SecurityEvent {

public AccountAutoUnlockedEvent(
    String username,
    String entityId) {

super(username, "USER", entityId);
}

@Override
public String action() {
return "ACCOUNT_AUTO_UNLOCKED";
}

@Override
public String details() {
return "Account automatically unlocked";
}
}