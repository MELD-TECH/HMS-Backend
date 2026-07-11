package com.hms.notification.mfa.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.hms.common.exception.OtpCooldownException;
import com.hms.common.exception.OtpResendLimitExceededException;
import com.hms.events.security.OtpCooldownEvent;
import com.hms.events.security.OtpResendLimitExceededEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.notification.mfa.config.MfaProperties;
import com.hms.notification.mfa.entity.OtpCode;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpResendValidator {

    private final MfaProperties properties;
    
    private final SecurityEventPublisher publisher;

    public void validate(
            OtpCode otp) {

        validateResendLimit(otp);

        validateCooldown(otp);

    }

    private void validateResendLimit(
            OtpCode otp) {

        if (otp.getResendCount() >= properties.getMaxResends()) {

            publisher.publish(

                new OtpResendLimitExceededEvent(

                    SecurityUtils.getCurrentUsername(),

                    otp.getUserId().toString()));

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

            publisher.publish(

                new OtpCooldownEvent(

                    SecurityUtils.getCurrentUsername(),

                    otp.getUserId().toString()));

            throw new OtpCooldownException();
        }
    }

}