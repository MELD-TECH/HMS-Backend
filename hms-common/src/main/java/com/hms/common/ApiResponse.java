package com.hms.common;

public record ApiResponse<T>( boolean success,
        String message,
        T data) {

}
