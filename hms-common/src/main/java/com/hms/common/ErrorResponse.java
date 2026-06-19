package com.hms.common;

import java.time.LocalDateTime;

public record ErrorResponse( String code,
        String message,
        String path,
        LocalDateTime timestamp) {

}
