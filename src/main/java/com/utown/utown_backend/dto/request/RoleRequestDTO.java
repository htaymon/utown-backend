package com.utown.utown_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;
}
