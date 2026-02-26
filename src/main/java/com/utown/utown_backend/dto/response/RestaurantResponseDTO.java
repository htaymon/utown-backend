package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.RestaurantStatus;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class RestaurantResponseDTO {

    private final long id;
    private long userId;
    private final String userName;
    private final Long categoryId;
    private final String categoryName;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final Double minimumOrder;
    private final RestaurantStatus status;
}