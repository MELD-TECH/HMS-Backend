package com.hms.common;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record ErrorResponse( String code,
        String message,
        String path,
        LocalDateTime timestamp) {

}
