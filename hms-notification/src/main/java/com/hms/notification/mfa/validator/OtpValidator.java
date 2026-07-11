package com.hms.notification.mfa.validator;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.InvalidOtpException;
import com.hms.common.exception.OtpExpiredException;
import com.hms.common.exception.OtpRetryLimitExceededException;
import com.hms.notification.mfa.config.MfaProperties;
import com.hms.notification.mfa.entity.OtpCode;
import com.hms.notification.mfa.enums.OtpStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OtpValidator {

	public final MfaProperties properties;

    public void validateFormat(String otp) {

        if (otp == null || otp.isBlank()) {

            throw new BusinessException(
                    "OTP is required");
        }

        if (!otp.matches("\\d{6}")) {

            throw new BusinessException(
                    "Invalid OTP format");
        }

    }
    
    public void validate(
            OtpCode otp,
            String code) {

        if (otp == null)

            throw new InvalidOtpException();

        validateFormat(code);

        validateActive(otp);

        validateExpiry(otp);

        validateCode(otp, code);

    }
    
    private void validateActive(

            OtpCode otp) {

        if (otp.getStatus()

                != OtpStatus.ACTIVE) {

            throw new InvalidOtpException();

        }

    }
    
    private void validateExpiry(

            OtpCode otp) {

        if (otp.getExpiresAt()

                .isBefore(LocalDateTime.now())) {
            
            throw new OtpExpiredException();
        }

    }
    
    private void validateCode(

            OtpCode otp,

            String code) {

        if (!otp.getCode().equals(code)) {

            throw new InvalidOtpException();

        }

    }
   
    public void validateAttempts(

            OtpCode otp) {

        if (otp.getAttempts()

                >= properties.getMaxAttempts()) {

            throw new OtpRetryLimitExceededException();

        }

    }
   
}