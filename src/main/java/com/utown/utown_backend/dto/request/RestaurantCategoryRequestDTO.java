package com.utown.utown_backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantCategoryRequestDTO {

    private String name;
    private String imageUrl;
    private Integer priority;
}