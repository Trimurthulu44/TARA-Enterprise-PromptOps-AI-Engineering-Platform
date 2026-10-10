package com.tara.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Enterprise standard API response envelope.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    @Builder.Default
    private boolean success = true;

    private String message;

    private T data;

    @Builder.Default
    private Instant timestamp = Instant.now();

    @Builder.Default
    private String traceId = UUID.randomUUID().toString();

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Success")
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }
}
