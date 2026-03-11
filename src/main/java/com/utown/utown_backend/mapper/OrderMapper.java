package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.OrderResponseDTO;
import org.mapstruct.*;
import com.utown.utown_backend.entity.*;

@Mapper(componentModel = "spring", uses = OrderItemMapper.class)
public interface OrderMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id")
    @Mapping(target = "items", source = "orderItems")
    OrderResponseDTO toResponseDTO(Order order);
}