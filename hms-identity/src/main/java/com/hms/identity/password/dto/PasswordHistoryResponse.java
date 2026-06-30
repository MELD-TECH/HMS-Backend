package com.hms.identity.password.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PasswordHistoryResponse(

        UUID id,

        LocalDateTime changedAt

) {
}