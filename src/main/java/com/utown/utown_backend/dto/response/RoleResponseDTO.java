package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing a role")
@Getter
@AllArgsConstructor
@Builder
public class RoleResponseDTO {

    @Schema(description = "Role ID", example = "1")
    private Long id;

    @Schema(description = "Role name", example = "ADMIN")
    private String name;
}
