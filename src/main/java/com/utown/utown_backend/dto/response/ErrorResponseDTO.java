package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "Standard error response returned when an API request fails")
@Getter
@AllArgsConstructor
public class ErrorResponseDTO {

    @Schema(description = "Error code identifier", example = "VALIDATION_ERROR")
    private String code;

    @Schema(description = "Error message describing what went wrong", example = "Invalid request body")
    private String message;

    @Schema(description = "API endpoint path where the error occurred", example = "/auth/register")
    private String path;

    @Schema(description = "Time when the error occurred", example = "2026-03-26T12:00:00")
    private LocalDateTime timestamp;
}
