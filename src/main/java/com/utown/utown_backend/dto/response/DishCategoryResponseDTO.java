package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class DishCategoryResponseDTO {

    private Long id;
    private final String name;
    private final String imageUrl;
    private final Integer priority;
}