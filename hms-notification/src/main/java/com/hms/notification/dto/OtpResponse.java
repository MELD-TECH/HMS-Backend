package com.hms.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record OtpResponse(

        UUID otpId,

        LocalDateTime expiresAt) {

}
