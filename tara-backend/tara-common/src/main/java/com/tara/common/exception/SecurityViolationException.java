package com.tara.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class SecurityViolationException extends RuntimeException {
    private final String violationType;
    private final String severity;

    public SecurityViolationException(String message, String violationType, String severity) {
        super(message);
        this.violationType = violationType;
        this.severity = severity;
    }

    public String getViolationType() {
        return violationType;
    }

    public String getSeverity() {
        return severity;
    }
}
