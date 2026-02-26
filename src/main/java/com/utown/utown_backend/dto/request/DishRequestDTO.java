package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.DishStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishRequestDTO {

    private Long restaurantId;
    private Long dishCategoryId;
    private String name;
    private String description;
    private Double price;
    private String image;
    private DishStatus status;
    private Integer priority;
}