package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request object for updating order status")
@Getter
@Setter
public class OrderStatusUpdateDTO {

    @Schema(
            description = "New order status",
            example = "CONFIRMED"
    )
    private OrderStatus status;
}
