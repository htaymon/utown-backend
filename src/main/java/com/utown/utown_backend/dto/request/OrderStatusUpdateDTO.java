package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateDTO {
    private OrderStatus status;
}
