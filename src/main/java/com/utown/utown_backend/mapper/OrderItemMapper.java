package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.OrderItemResponseDTO;
import org.mapstruct.*;
import com.utown.utown_backend.entity.*;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "dishId", source = "dish.id")
    @Mapping(target = "dishName", source = "dish.name")
    OrderItemResponseDTO toResponseDTO(OrderItem orderItem);
}