package com.utown.utown_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ErrorResponseDTO {

    private String path;
    private String message;
    private int status;
    private LocalDateTime timestamp;
}
