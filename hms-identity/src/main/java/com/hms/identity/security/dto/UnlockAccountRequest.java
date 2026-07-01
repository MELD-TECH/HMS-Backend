package com.hms.identity.security.dto;

import java.util.UUID;

public record UnlockAccountRequest(

        UUID userId) {
}