package com.hms.common.exception;

public class OtpResendLimitExceededException
extends BusinessException {

public OtpResendLimitExceededException() {

super("Maximum OTP resend limit exceeded.");

}

}
