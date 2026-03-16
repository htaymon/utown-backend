package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.RestaurantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequestDTO {

    @NotNull(message = "Restaurant Category ID is required")
    private Long restaurantCategoryId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Minimum Order is required")
    private Double minimumOrder;

    @NotNull(message = "Restaurant Status is required")
    private RestaurantStatus status;
}