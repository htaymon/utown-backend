package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing a restaurant category")
@Getter
@AllArgsConstructor
@Builder
public class RestaurantCategoryResponseDTO {

    @Schema(description = "Category ID", example = "1")
    private final long id;

    @Schema(description = "Category name", example = "Korean Food")
    private final String name;

    @Schema(description = "Category image URL", example = "https://example.com/korean-food.jpg")
    private final String imageUrl;

    @Schema(description = "Display priority", example = "1")
    private final Integer priority;
}