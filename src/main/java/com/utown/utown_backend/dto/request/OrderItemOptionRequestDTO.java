package com.utown.utown_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemOptionRequestDTO {

    @NotNull(message = "Order Item ID is required")
    private Long orderItemId;

    @NotNull(message = "Option ID is required")
    private Long optionId;
}