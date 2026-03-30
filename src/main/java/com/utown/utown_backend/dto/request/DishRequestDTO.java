package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.DishStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "Request object for creating or updating a dish")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishRequestDTO {

    @Schema(description = "Restaurant ID for the dish", example = "1")
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @Schema(description = "Dish category ID", example = "2")
    @NotNull(message = "Dish Category ID is required")
    private Long dishCategoryId;

    @Schema(description = "Dish name", example = "Spicy Chicken")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Dish description", example = "Crispy fried chicken with spicy sauce")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Dish price", example = "12.99")
    @NotNull(message = "Price is required")
    private Double price;

    @Schema(description = "Image URL", example = "https://example.com/dish.jpg")
    @NotNull(message = "Image is required")
    private String image;

    @Schema(description = "Dish status (AVAILABLE, UNAVAILABLE)", example = "AVAILABLE")
    @NotNull(message = "Status is required")
    private DishStatus status;

    @Schema(description = "Display priority ", example = "1")
    @NotNull(message = "Priority is required")
    private Integer priority;
}