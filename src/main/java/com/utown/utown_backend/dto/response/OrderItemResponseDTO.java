package com.utown.utown_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponseDTO {

    private Long id;
    private Long dishId;
    private String dishName;
    private Integer quantity;
    private BigDecimal price;

}