package com.hms.common.exception;

public class AuthenticationChallengeCompletedException
        extends BusinessException {

    public AuthenticationChallengeCompletedException() {

        super("Authentication challenge has already been completed.");
    }
}