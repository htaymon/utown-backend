package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.OrderStatus;
import lombok.*;

import java.util.List;

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

    private List<OrderItemResponseDTO> items;

}