package com.hms.identity.security.event;

public class UserUpdateEvent extends SecurityEvent{
		
	public UserUpdateEvent(
	    String username,
	    String entityId) {

	super(username,"USER",entityId);
	}

	@Override
	public String action() {

	return "USER_UPDATED";
	}

	@Override
	public String details() {

	return "Updated user";
	
   }

}
