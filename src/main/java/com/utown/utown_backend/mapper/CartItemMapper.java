package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import org.mapstruct.*;
import com.utown.utown_backend.entity.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "dish", ignore = true)
    CartItem toEntity(CartItemRequestDTO dto);

    @Mapping(target = "cartId", source = "cart.id")
    @Mapping(target = "dishId", source = "dish.id")
    @Mapping(target = "dishName", source = "dish.name")
    CartItemResponseDTO toResponseDTO(CartItem item);

    List<CartItemResponseDTO> toResponseList(List<CartItem> items);
}