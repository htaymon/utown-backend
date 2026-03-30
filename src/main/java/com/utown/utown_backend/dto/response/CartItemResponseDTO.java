package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing a cart item")
@Getter
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {

    @Schema(description = "Cart item ID", example = "1")
    private Long id;

    @Schema(description = "Cart ID", example = "1")
    private Long cartId;

    @Schema(description = "Dish ID", example = "10")
    private Long dishId;

    @Schema(description = "Dish name", example = "Spicy Chicken")
    private String dishName;

    @Schema(description = "Quantity of the dish", example = "2")
    private Integer quantity;
}
