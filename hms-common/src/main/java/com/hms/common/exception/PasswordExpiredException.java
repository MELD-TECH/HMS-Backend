package com.hms.common.exception;

public class PasswordExpiredException
        extends BusinessException {

    public PasswordExpiredException() {

        super("Your password has expired. Please reset your password.");

    }

}
