package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.DishStatus;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class DishResponseDTO {

    private  final Long id;
    private final Long restaurantId;
    private final String restaurantName;
    private final Long dishCategoryId;
    private final String dishCategoryName;
    private final String name;
    private final String description;
    private final Double price;
    private final String image;
    private final DishStatus status;
    private final Integer priority;
}