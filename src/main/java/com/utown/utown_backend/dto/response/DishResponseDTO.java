package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.DishStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing a dish")
@Getter
@AllArgsConstructor
@Builder
public class DishResponseDTO {

    @Schema(description = "Dish ID", example = "1")
    private  final Long id;

    @Schema(description = "Restaurant ID", example = "1")
    private final Long restaurantId;

    @Schema(description = "Restaurant name", example = "BBQ Chicken")
    private final String restaurantName;

    @Schema(description = "Dish category ID", example = "2")
    private final Long dishCategoryId;

    @Schema(description = "Dish category name", example = "Main Course")
    private final String dishCategoryName;

    @Schema(description = "Dish name", example = "Spicy Chicken")
    private final String name;

    @Schema(description = "Dish description", example = "Crispy fried chicken with spicy sauce")
    private final String description;

    @Schema(description = "Dish price", example = "12.99")
    private final Double price;

    @Schema(description = "Image URL", example = "https://example.com/dish.jpg")
    private final String image;

    @Schema(description = "Dish status", example = "AVAILABLE")
    private final DishStatus status;

    @Schema(description = "Display priority", example = "1")
    private final Integer priority;
}