package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.OrderRequestDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import org.mapstruct.*;
import com.utown.utown_backend.entity.*;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "deliveryAddress", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Order toEntity(OrderRequestDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    @Mapping(target = "deliveryAddressId", source = "deliveryAddress.id")
    OrderResponseDTO toResponseDTO(Order order);
}