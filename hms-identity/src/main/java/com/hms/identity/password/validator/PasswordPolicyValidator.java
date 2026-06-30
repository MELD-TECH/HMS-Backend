package com.hms.identity.password.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.hms.common.exception.BusinessException;
import com.hms.identity.password.config.PasswordPolicyProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordPolicyValidator {

    private final PasswordPolicyProperties properties;

    private static final Pattern UPPER =
            Pattern.compile(".*[A-Z].*");

    private static final Pattern LOWER =
            Pattern.compile(".*[a-z].*");

    private static final Pattern DIGIT =
            Pattern.compile(".*\\d.*");

    private static final Pattern SPECIAL =
            Pattern.compile(".*[^A-Za-z0-9].*");

    public void validate(
            String password) {

        if (password.length()
                < properties.getMinimumLength()) {

            throw new BusinessException(
                    "Password must contain at least "
                            + properties.getMinimumLength()
                            + " characters");
        }

        if (password.length()
                > properties.getMaximumLength()) {

            throw new BusinessException(
                    "Password exceeds maximum length");
        }

        if (properties.isRequireUppercase()
                && !UPPER.matcher(password).matches()) {

            throw new BusinessException(
                    "Password must contain an uppercase letter");
        }

        if (properties.isRequireLowercase()
                && !LOWER.matcher(password).matches()) {

            throw new BusinessException(
                    "Password must contain a lowercase letter");
        }

        if (properties.isRequireDigit()
                && !DIGIT.matcher(password).matches()) {

            throw new BusinessException(
                    "Password must contain a digit");
        }

        if (properties.isRequireSpecialCharacter()
                && !SPECIAL.matcher(password).matches()) {

            throw new BusinessException(
                    "Password must contain a special character");
        }
    }
}