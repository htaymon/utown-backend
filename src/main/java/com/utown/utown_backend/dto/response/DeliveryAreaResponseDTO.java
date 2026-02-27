package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class DeliveryAreaResponseDTO {

    private Long id;
    private final Long restaurantId;
    private final String restaurantName;
    private final String city;
    private final String name;
}