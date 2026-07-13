package com.hms.common.exception;

public class AuthenticationChallengeExpiredException
        extends BusinessException {

    public AuthenticationChallengeExpiredException() {

        super("Authentication challenge has expired.");
    }
}
