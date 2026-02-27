package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.RestaurantStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantRequestDTO {

    private Long userId;
    private Long restaurantCategoryId;
    private String name;
    private String description;
    private String imageUrl;
    private Double minimumOrder;
    private RestaurantStatus status;
}