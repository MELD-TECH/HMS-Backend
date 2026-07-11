package com.hms.common.exception;

public class OtpExpiredException

extends BusinessException{

    public OtpExpiredException(){

        super("Verification code has expired.");

    }

}