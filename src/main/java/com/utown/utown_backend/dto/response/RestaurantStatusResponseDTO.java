package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.RestaurantStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RestaurantStatusResponseDTO {
    private Long restaurantId;
    private RestaurantStatus status;
}
