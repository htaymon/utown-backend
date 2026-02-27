package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T02:52:08+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Autowired
    private CartItemMapper cartItemMapper;

    @Override
    public Cart toEntity(CartRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cart.CartBuilder cart = Cart.builder();

        return cart.build();
    }

    @Override
    public CartResponseDTO toResponseDTO(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponseDTO.CartResponseDTOBuilder cartResponseDTO = CartResponseDTO.builder();

        cartResponseDTO.userId( cartUserId( cart ) );
        cartResponseDTO.restaurantId( cartRestaurantId( cart ) );
        cartResponseDTO.id( cart.getId() );
        cartResponseDTO.cartItems( cartItemMapper.toResponseList( cart.getCartItems() ) );

        return cartResponseDTO.build();
    }

    @Override
    public List<CartResponseDTO> toResponseList(List<Cart> items) {
        if ( items == null ) {
            return null;
        }

        List<CartResponseDTO> list = new ArrayList<CartResponseDTO>( items.size() );
        for ( Cart cart : items ) {
            list.add( toResponseDTO( cart ) );
        }

        return list;
    }

    private Long cartUserId(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        User user = cart.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long cartRestaurantId(Cart cart) {
        if ( cart == null ) {
            return null;
        }
        Restaurant restaurant = cart.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        Long id = restaurant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
