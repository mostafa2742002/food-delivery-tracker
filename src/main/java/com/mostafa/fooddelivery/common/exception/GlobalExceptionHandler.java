package com.mostafa.fooddelivery.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle Resource Not Found (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        log.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle Bad Request (400)
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(
            BadRequestException ex,
            HttpServletRequest request
    ) {
        log.error("Bad request: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle Unauthorized (401)
     */
    @ExceptionHandler({UnauthorizedException.class, BadCredentialsException.class})
    public ResponseEntity<ErrorResponse> handleUnauthorized(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unauthorized: {}", ex.getMessage());
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message("Invalid email or password")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle Validation Errors (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        log.error("Validation errors: {}", validationErrors);

        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Please check the input fields")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle Runtime Exceptions (500)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        log.error("Runtime exception: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Handle all other exceptions (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}