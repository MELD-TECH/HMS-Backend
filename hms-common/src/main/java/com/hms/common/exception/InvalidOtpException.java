package com.hms.common.exception;

public class InvalidOtpException
extends BusinessException{

    public InvalidOtpException(){

        super("Invalid verification code.");

    }

}