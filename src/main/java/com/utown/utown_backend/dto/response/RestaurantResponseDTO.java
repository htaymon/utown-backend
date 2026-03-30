package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.RestaurantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing a restaurant")
@Getter
@AllArgsConstructor
@Builder
public class RestaurantResponseDTO {

    @Schema(description = "Restaurant ID", example = "1")
    private final long id;

    @Schema(description = "Owner (user) ID", example = "10")
    private long userId;

    @Schema(description = "Owner name", example = "John Doe")
    private final String userName;

    @Schema(description = "Category ID", example = "1")
    private final Long categoryId;

    @Schema(description = "Category name", example = "Korean Food")
    private final String categoryName;

    @Schema(description = "Restaurant name", example = "Seoul BBQ")
    private final String name;

    @Schema(description = "Restaurant description", example = "Authentic Korean BBQ restaurant")
    private final String description;

    @Schema(description = "Restaurant image URL", example = "https://example.com/restaurant.jpg")
    private final String imageUrl;

    @Schema(description = "Minimum order amount", example = "15.00")
    private final Double minimumOrder;

    @Schema(description = "Restaurant status", example = "OPEN")
    private final RestaurantStatus status;
}