package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.RestaurantStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestaurantStatusUpdateDTO {
    private RestaurantStatus status;
}
