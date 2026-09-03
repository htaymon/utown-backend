package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Schema(description = "Request object for creating or updating a restaurant category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantCategoryRequestDTO {

    @Schema(description = "Category name", example = "Korean Food")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Category image URL", example = "https://example.com/korean-food.jpg")
    @NotNull(message = "Image is required")
    private String imageUrl;

    @Schema(description = "Display priority", example = "1")
    @NotNull(message = "Priority is required")
    @PositiveOrZero(message = "Priority must be 0 or greater")
    private Integer priority;
}