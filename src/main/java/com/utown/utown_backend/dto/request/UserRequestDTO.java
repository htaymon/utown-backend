package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Schema(description = "Request object for creating or updating a user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {

    @Schema(description = "User full name", example = "James")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "User email address", example = "james@example.com")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(
            description = "User password",
            example = "password123",
            format = "password"
    )
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Schema(description = "Phone number", example = "09123456789")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @Schema(description = "Role ID assigned to user", example = "1")
    @NotNull(message = "Role ID is required")
    private Long roleId;
}