package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Response object representing order status")
@Getter
@Builder
public class OrderStatusResponseDTO {

    @Schema(description = "Order ID", example = "1")
    private Long orderId;

    @Schema(
            description = "Current order status (PENDING,CONFIRMED,COOKING,DELIVERING,COMPLETED,CANCELLED)",
            example = "CONFIRMED"
    )
    private OrderStatus status;
}
