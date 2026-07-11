package com.hms.notification.mfa.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.hms.common.exception.OtpCooldownException;
import com.hms.common.exception.OtpResendLimitExceededException;
import com.hms.notification.mfa.config.MfaProperties;
import com.hms.notification.mfa.entity.OtpCode;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpResendValidator {

    private final MfaProperties properties;

    public void validate(
            OtpCode otp) {

        validateResendLimit(otp);

        validateCooldown(otp);

    }

    private void validateResendLimit(
            OtpCode otp) {

        if (otp.getResendCount() >= properties.getMaxResends()) {

            throw new OtpResendLimitExceededException();

        }

    }

    private void validateCooldown(
            OtpCode otp) {

        if (otp.getLastResentAt() == null) {

            return;

        }

        LocalDateTime allowedTime =

                otp.getLastResentAt()

                        .plusSeconds(

                                properties.getResendCooldownSeconds());

        if (LocalDateTime.now().isBefore(allowedTime)) {

            throw new OtpCooldownException();

        }

    }

}