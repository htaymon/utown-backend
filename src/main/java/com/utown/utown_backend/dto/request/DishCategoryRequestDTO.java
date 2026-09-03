package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Schema(description = "Request object for creating or updating a dish category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishCategoryRequestDTO {

    @Schema(description = "Category name", example = "Main Course")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Image URL of the category", example = "https://example.com/category.jpg")
    @NotNull(message = "Image URL is required")
    private String imageUrl;

    @Schema(description = "Display priority", example = "1")
    @NotNull(message = "Priority is required")
    @PositiveOrZero(message = "Priority must be 0 or greater")
    private Integer priority;
}