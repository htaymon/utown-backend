package com.utown.utown_backend.dto.response;

import lombok.*;

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
    private Double price;

}