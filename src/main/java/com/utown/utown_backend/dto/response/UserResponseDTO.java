package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing a user")
@Getter
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    @Schema(description = "User ID", example = "1")
    private final long id;

    @Schema(description = "User full name", example = "James")
    private final String name;

    @Schema(description = "User email", example = "james@example.com")
    private final String email;

    @Schema(description = "Phone number", example = "09123456789")
    private final String phoneNumber;

    @Schema(description = "Role name", example = "CLIENT")
    private final String roleName;
}