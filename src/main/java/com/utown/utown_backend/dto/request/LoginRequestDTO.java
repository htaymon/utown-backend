package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request object for user login")
@Getter
@Setter
public class LoginRequestDTO {

    @Schema(description = "User email address", example = "user@example.com")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "User password", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;
}
