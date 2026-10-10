package com.tara.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Enterprise standard error representation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String errorCode;
    private String message;
    private List<String> details;
    private String path;
    private String traceId;

    @Builder.Default
    private Instant timestamp = Instant.now();
}
