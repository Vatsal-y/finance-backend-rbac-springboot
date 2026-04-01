package com.zorvyn.finance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "Always false for error responses", example = "false")
    @Builder.Default
    private boolean success = false;

    @Schema(description = "Error message", example = "Validation failed")
    private String message;

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Timestamp of the error")
    private LocalDateTime timestamp;

    @Schema(description = "Field-level validation errors")
    private Map<String, String> errors;
}
