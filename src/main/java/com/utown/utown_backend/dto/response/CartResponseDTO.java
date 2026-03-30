package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.List;

@Schema(description = "Response object representing a cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {

    @Schema(description = "Cart ID", example = "1")
    private Long id;

    @Schema(description = "User ID who owns the cart", example = "7")
    private Long userId;

    @Schema(description = "Restaurant ID associated with the cart", example = "1")
    private Long restaurantId;

    @Schema(description = "List of items in the cart")
    private List<CartItemResponseDTO> cartItems;
}