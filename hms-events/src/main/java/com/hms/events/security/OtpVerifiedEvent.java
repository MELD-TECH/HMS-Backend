package com.hms.events.security;

public class OtpVerifiedEvent

extends SecurityEvent{

	public OtpVerifiedEvent(String username, String entityId) {
	super(username, "USER", entityId);
	}

    @Override

    public String action(){

        return "OTP_VERIFIED";

    }

    @Override

    public String details(){

        return "OTP successfully verified";

    }

}
