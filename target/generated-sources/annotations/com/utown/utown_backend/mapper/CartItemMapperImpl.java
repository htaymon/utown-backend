package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.CartItem;
import com.utown.utown_backend.entity.Dish;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T17:21:11+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Homebrew)"
)
@Component
public class CartItemMapperImpl implements CartItemMapper {

    @Override
    public CartItem toEntity(CartItemRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CartItem.CartItemBuilder cartItem = CartItem.builder();

        cartItem.quantity( dto.getQuantity() );

        return cartItem.build();
    }

    @Override
    public CartItemResponseDTO toResponseDTO(CartItem item) {
        if ( item == null ) {
            return null;
        }

        CartItemResponseDTO.CartItemResponseDTOBuilder cartItemResponseDTO = CartItemResponseDTO.builder();

        cartItemResponseDTO.cartId( itemCartId( item ) );
        cartItemResponseDTO.dishId( itemDishId( item ) );
        cartItemResponseDTO.dishName( itemDishName( item ) );
        cartItemResponseDTO.id( item.getId() );
        cartItemResponseDTO.quantity( item.getQuantity() );

        return cartItemResponseDTO.build();
    }

    @Override
    public List<CartItemResponseDTO> toResponseList(List<CartItem> items) {
        if ( items == null ) {
            return null;
        }

        List<CartItemResponseDTO> list = new ArrayList<CartItemResponseDTO>( items.size() );
        for ( CartItem cartItem : items ) {
            list.add( toResponseDTO( cartItem ) );
        }

        return list;
    }

    private Long itemCartId(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Cart cart = cartItem.getCart();
        if ( cart == null ) {
            return null;
        }
        Long id = cart.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long itemDishId(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Dish dish = cartItem.getDish();
        if ( dish == null ) {
            return null;
        }
        Long id = dish.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String itemDishName(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }
        Dish dish = cartItem.getDish();
        if ( dish == null ) {
            return null;
        }
        String name = dish.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
