package com.hms.common.handler;

import com.hms.common.ErrorResponse;
import com.hms.common.exception.AccountLockedException;
import com.hms.common.exception.BusinessException;
import com.hms.common.exception.InvalidRefreshTokenException;
import com.hms.common.exception.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log =
            LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse response = new ErrorResponse(
                "RESOURCE_NOT_FOUND",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException ex,
            HttpServletRequest request) {

        log.warn("Business validation error: {}", ex.getMessage());

        ErrorResponse response = new ErrorResponse(
                "BUSINESS_ERROR",
                ex.getMessage(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		log.warn("Validation error: {}", ex.getMessage());

		ErrorResponse response = new ErrorResponse("VALIDATION_ERROR", ex.getMessage(), request.getRequestURI(),
				LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

    @ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex,
			HttpServletRequest request) {

		log.warn("Access denied: {}", ex.getMessage());

		ErrorResponse response = new ErrorResponse("ACCESS_DENIED", ex.getMessage(), request.getRequestURI(),
				LocalDateTime.now());

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
	}

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDenied(
            AuthorizationDeniedException ex,
            HttpServletRequest request) {
    	
    	log.warn("Authorization denied: {}", ex.getMessage());
        ErrorResponse response =
                new ErrorResponse(
                        "ACCESS_DENIED",
                        "You do not have permission to perform this operation",
                        request.getRequestURI(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error occurred", ex);

        ErrorResponse response = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred. Please contact support.",
                request.getRequestURI(),
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
   
    @ExceptionHandler(
            InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse>
    handleInvalidRefreshToken(

            InvalidRefreshTokenException ex,
            HttpServletRequest request) {

        ErrorResponse response =
        		new ErrorResponse(        
                       "INVALID_REFRESH_TOKEN",
                        ex.getMessage(),
                        request.getRequestURI(),
                        LocalDateTime.now()                        
        				);
                        

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
    		BadCredentialsException ex,
            HttpServletRequest request) {

        ErrorResponse response =
                new ErrorResponse(
                        "INVALID_CREDENTIALS",
                        "Invalid username or password",
                        request.getRequestURI(),
                        LocalDateTime.now()
                );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
    
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ErrorResponse> handleLocked(

            AccountLockedException ex,

            HttpServletRequest request) {

        return ResponseEntity

                .status(423)

                .body(

                        ErrorResponse.builder()

                                .code("ACCOUNT_LOCKED")

                                .message(ex.getMessage())

                                .path(request.getRequestURI())

                                .timestamp(LocalDateTime.now())

                                .build());
    }
}