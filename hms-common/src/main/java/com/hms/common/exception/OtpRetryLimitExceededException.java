package com.hms.common.exception;

public class OtpRetryLimitExceededException

extends BusinessException{

    public OtpRetryLimitExceededException(){

        super("Maximum verification attempts exceeded.");

    }

}
