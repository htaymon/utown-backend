package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.OrderItemRequestDTO;
import com.utown.utown_backend.dto.response.OrderItemResponseDTO;
import org.mapstruct.*;
import com.utown.utown_backend.entity.*;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "dish.id", source = "dishId")
    @Mapping(target = "orderItemOptions", source = "options")
    OrderItem toEntity(OrderItemRequestDTO dto);

    @Mapping(target = "orderId", source = "order.id")
    @Mapping(target = "dishId", source = "dish.id")
    @Mapping(target = "dishName", source = "dish.name")
    @Mapping(target = "options", source = "orderItemOptions")
    OrderItemResponseDTO toResponseDTO(OrderItem entity);
}