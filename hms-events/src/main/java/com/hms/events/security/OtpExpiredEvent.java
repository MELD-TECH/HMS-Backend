package com.hms.events.security;

public class OtpExpiredEvent extends SecurityEvent {

	public OtpExpiredEvent(String username, String entityId) {
	super(username, "USER", entityId);
	}

    @Override

    public String action(){

        return "OTP_Expired";

    }

    @Override

    public String details(){

        return "Expired OTP";

    }

}
