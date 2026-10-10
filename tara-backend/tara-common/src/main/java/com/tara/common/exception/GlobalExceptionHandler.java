package com.tara.common.exception;

import com.tara.common.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", ex.getMessage(), null, request);
    }

    @ExceptionHandler(TenantMismatchException.class)
    public ResponseEntity<ApiError> handleTenantMismatch(TenantMismatchException ex, HttpServletRequest request) {
        log.warn("Tenant mismatch: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "TENANT_MISMATCH", ex.getMessage(), null, request);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request) {
        log.warn("Unauthorized access: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", ex.getMessage(), null, request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to perform this action", null, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidation(ValidationException ex, HttpServletRequest request) {
        log.warn("Validation error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", ex.getMessage(), ex.getErrors(), request);
    }

    @ExceptionHandler(SecurityViolationException.class)
    public ResponseEntity<ApiError> handleSecurityViolation(SecurityViolationException ex, HttpServletRequest request) {
        log.warn("Security violation detected [{}]: {}", ex.getViolationType(), ex.getMessage());
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "SECURITY_VIOLATION_" + ex.getViolationType(), ex.getMessage(), List.of("Severity: " + ex.getSeverity()), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.toList());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENTS", "Validation failed for request body", details, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception ex, HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        log.error("Unhandled server exception [traceId={}]: {}", traceId, ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiError.builder()
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please contact enterprise support.")
                .details(List.of(ex.getMessage() != null ? ex.getMessage() : "Unknown error"))
                .path(request.getRequestURI())
                .traceId(traceId)
                .timestamp(Instant.now())
                .build()
        );
    }

    private ResponseEntity<ApiError> buildErrorResponse(HttpStatus status, String code, String message, List<String> details, HttpServletRequest request) {
        return ResponseEntity.status(status).body(
            ApiError.builder()
                .errorCode(code)
                .message(message)
                .details(details)
                .path(request.getRequestURI())
                .traceId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .build()
        );
    }
}
