package com.hms.identity.password.dto;

public record PasswordPolicyResponse(

        int minimumLength,

        int maximumLength,

        boolean requireUppercase,

        boolean requireLowercase,

        boolean requireDigit,

        boolean requireSpecialCharacter,

        int historySize,

        int expiryDays

) {
}