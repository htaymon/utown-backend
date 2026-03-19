package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderStatusResponseDTO {
    private Long orderId;
    private OrderStatus status;
}
