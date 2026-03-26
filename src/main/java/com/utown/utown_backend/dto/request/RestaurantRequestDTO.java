package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.RestaurantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Schema(description = "Request object for creating or updating a restaurant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequestDTO {

    @Schema(description = "Restaurant category ID", example = "1")
    @NotNull(message = "Restaurant Category ID is required")
    private Long restaurantCategoryId;

    @Schema(description = "Restaurant name", example = "Seoul BBQ")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Restaurant description", example = "Authentic Korean BBQ restaurant")
    @NotBlank(message = "Description is required")
    private String description;

    @Schema(description = "Restaurant image URL", example = "https://example.com/restaurant.jpg")
    @NotNull(message = "Image URL is required")
    private String imageUrl;

    @Schema(description = "Minimum order amount", example = "15.00")
    @NotNull(message = "Minimum Order is required")
    @Positive(message = "Minimum order must be greater than 0")
    private Double minimumOrder;

    @Schema(description = "Restaurant status (OPEN, CLOSED)", example = "OPEN")
    @NotNull(message = "Restaurant Status is required")
    private RestaurantStatus status;
}