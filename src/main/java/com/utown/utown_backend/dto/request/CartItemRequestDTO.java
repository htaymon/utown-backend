package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Schema(description = "Request object for creating or updating a cart item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDTO {

    @Schema(description = "Cart ID", example = "1")
    @NotNull(message = "Cart ID is required")
    private Long cartId;

    @Schema(description = "Dish ID", example = "10")
    @NotNull(message = "Dish ID is required")
    private Long dishId;

    @Schema(description = "Quantity of the dish", example = "2")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private Integer quantity;
}
