package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class CartItemResponseDTO {

    private Long id;
    private Long cartId;
    private Long dishId;
    private String dishName;
    private Integer quantity;
}
