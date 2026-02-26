package com.utown.utown_backend.dto;

import com.utown.utown_backend.dto.request.OrderItemOptionRequestDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemRequestDTO {

    private Long orderId;
    private Long dishId;
    private Integer quantity;
    private Double price;

    private List<OrderItemOptionRequestDTO> options;
}