package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class RestaurantCategoryResponseDTO {

    private final long id;
    private final String name;
    private final String imageUrl;
    private final Integer priority;
}