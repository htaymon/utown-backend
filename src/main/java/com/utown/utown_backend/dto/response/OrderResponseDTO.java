package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Schema(description = "Response object representing an order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponseDTO {

    @Schema(description = "Order ID", example = "1")
    private Long id;

    @Schema(description = "Order ID", example = "1")
    private String orderNo;

    @Schema(description = "Unique order number", example = "27d20a15-c92b-450e-afb2-3b2c5cb944e6")
    private Long userId;

    @Schema(description = "Restaurant ID", example = "2")
    private Long restaurantId;

    @Schema(description = "Delivery address ID", example = "5")
    private Long deliveryAddressId;

    @Schema(description = "Order status", example = "PENDING")
    private OrderStatus status;

    @Schema(description = "Total price of the order", example = "25.50")
    private Double totalPrice;

    @Schema(description = "List of order items")
    private List<OrderItemResponseDTO> items;

}