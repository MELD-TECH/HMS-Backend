package com.hms.common.exception;

public class OtpCooldownException
extends BusinessException {

public OtpCooldownException() {

super("Please wait before requesting another OTP.");

}

}