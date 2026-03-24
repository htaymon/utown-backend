package com.utown.utown_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponseDTO {

    private String code;
    private String message;
    private String path;
    private LocalDateTime timestamp;
}
