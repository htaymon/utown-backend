package com.utown.utown_backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryAreaRequestDTO {

    private Long restaurantId;
    private String city;
    private String name;
}