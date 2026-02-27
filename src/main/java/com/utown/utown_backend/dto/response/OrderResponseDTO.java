package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.OrderStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    private Long id;
    private String orderNo;

    private Long userId;
    private Long restaurantId;
    private Long deliveryAddressId;

    private OrderStatus status;
    private Double totalPrice;
    private Integer cookingTime;
}