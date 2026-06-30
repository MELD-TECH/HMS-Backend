package com.hms.identity.password.service;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.hms.common.exception.BusinessException;
import com.hms.identity.password.config.PasswordPolicyProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordPolicyService {

    private final PasswordPolicyProperties properties;

    private static final Pattern UPPER =
            Pattern.compile("[A-Z]");

    private static final Pattern LOWER =
            Pattern.compile("[a-z]");

    private static final Pattern NUMBER =
            Pattern.compile("[0-9]");

    private static final Pattern SPECIAL =
            Pattern.compile("[^A-Za-z0-9]");

    public void validate(
            String password) {

        if (password.length() < properties.getMinimumLength()) {

            throw new BusinessException(
                    "Password must be at least "
                            + properties.getMinimumLength()
                            + " characters");

        }

        if (properties.isRequireUppercase()
                && !UPPER.matcher(password).find()) {

            throw new BusinessException(
                    "Password must contain an uppercase letter");

        }

        if (properties.isRequireLowercase()
                && !LOWER.matcher(password).find()) {

            throw new BusinessException(
                    "Password must contain a lowercase letter");

        }

        if (properties.isRequireDigit()
                && !NUMBER.matcher(password).find()) {

            throw new BusinessException(
                    "Password must contain a number");

        }

        if (properties.isRequireSpecialCharacter()
                && !SPECIAL.matcher(password).find()) {

            throw new BusinessException(
                    "Password must contain a special character");

        }

    }

}