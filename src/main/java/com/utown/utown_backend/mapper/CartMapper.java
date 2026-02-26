package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import org.mapstruct.*;
import com.utown.utown_backend.entity.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "restaurant", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    Cart toEntity(CartRequestDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "restaurantId", source = "restaurant.id")
    CartResponseDTO toResponseDTO(Cart cart);

    List<CartResponseDTO> toResponseList(List<Cart> items);
}