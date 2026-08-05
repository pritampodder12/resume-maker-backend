package com.resumebuilder.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API Response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Indicates if the request was successful", example = "true")
    private final boolean success;

    @Schema(description = "Human-readable message", example = "Resume created successfully")
    private final String message;

    @Schema(description = "Response data payload")
    private final T data;

    @Schema(description = "Timestamp of the response")
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Error details, if any")
    private final ErrorDetails error;

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Error details")
    public static class ErrorDetails {
        @Schema(description = "Error code", example = "VALIDATION_ERROR")
        private final String code;

        @Schema(description = "Error message")
        private final String message;

        @Schema(description = "Field-level validation errors")
        private final java.util.Map<String, String> fieldErrors;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiResponse<T> error(String message, String errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetails.builder()
                        .code(errorCode)
                        .message(message)
                        .build())
                .build();
    }

    public static <T> ApiResponse<T> error(String message, String errorCode, java.util.Map<String, String> fieldErrors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .error(ErrorDetails.builder()
                        .code(errorCode)
                        .message(message)
                        .fieldErrors(fieldErrors)
                        .build())
                .build();
    }
}
