package com.utown.utown_backend.dto.response;

import lombok.*;

import java.util.List;

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

   // private List<OrderItemOptionResponseDTO> options;
}