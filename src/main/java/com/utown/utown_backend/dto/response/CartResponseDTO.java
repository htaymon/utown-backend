package com.utown.utown_backend.dto.response;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponseDTO {

    private Long id;
    private Long userId;
    private Long restaurantId;

    private List<CartItemResponseDTO> cartItems;
}