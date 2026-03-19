package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.DishStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishRequestDTO {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;

    @NotNull(message = "Dish Category ID is required")
    private Long dishCategoryId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    private Double price;

    @NotNull(message = "Image is required")
    private String image;

    @NotNull(message = "Status is required")
    private DishStatus status;

    @NotNull(message = "Priority is required")
    private Integer priority;
}