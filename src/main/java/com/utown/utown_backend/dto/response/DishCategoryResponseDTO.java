package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing a dish category")
@Getter
@AllArgsConstructor
@Builder
public class DishCategoryResponseDTO {

    @Schema(description = "Category ID", example = "1")
    private Long id;

    @Schema(description = "Category name", example = "Main Course")
    private final String name;

    @Schema(description = "Image URL of the category", example = "https://example.com/category.jpg")
    private final String imageUrl;

    @Schema(description = "Display priority", example = "1")
    private final Integer priority;
}