package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "Request object for placing an order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @Schema(description = "Delivery address ID for the order", example = "5")
    @NotNull(message = "Delivery Address ID is required")
    private Long deliveryAddressId;

}