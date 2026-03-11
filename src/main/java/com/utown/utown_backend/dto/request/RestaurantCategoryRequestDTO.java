package com.utown.utown_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantCategoryRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Image is required")
    private String imageUrl;

    @NotNull(message = "Priority is required")
    private Integer priority;
}