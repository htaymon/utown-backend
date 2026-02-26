package com.utown.utown_backend.dto;

import com.utown.utown_backend.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    private Long userId;
    private Long restaurantId;
    private Long deliveryAddressId;

    private OrderStatus status;
    private Double totalPrice;
    private Integer cookingTime;
}