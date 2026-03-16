package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.OrderItemResponseDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.entity.Address;
import com.utown.utown_backend.entity.Order;
import com.utown.utown_backend.entity.OrderItem;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-13T15:50:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public OrderResponseDTO toResponseDTO(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponseDTO.OrderResponseDTOBuilder orderResponseDTO = OrderResponseDTO.builder();

        orderResponseDTO.userId( orderUserId( order ) );
        orderResponseDTO.restaurantId( orderRestaurantId( order ) );
        orderResponseDTO.deliveryAddressId( orderDeliveryAddressId( order ) );
        orderResponseDTO.items( orderItemListToOrderItemResponseDTOList( order.getOrderItems() ) );
        orderResponseDTO.id( order.getId() );
        orderResponseDTO.orderNo( order.getOrderNo() );
        orderResponseDTO.status( order.getStatus() );
        orderResponseDTO.totalPrice( order.getTotalPrice() );
        orderResponseDTO.cookingTime( order.getCookingTime() );

        return orderResponseDTO.build();
    }

    private Long orderUserId(Order order) {
        if ( order == null ) {
            return null;
        }
        User user = order.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long orderRestaurantId(Order order) {
        if ( order == null ) {
            return null;
        }
        Restaurant restaurant = order.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        Long id = restaurant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long orderDeliveryAddressId(Order order) {
        if ( order == null ) {
            return null;
        }
        Address deliveryAddress = order.getDeliveryAddress();
        if ( deliveryAddress == null ) {
            return null;
        }
        Long id = deliveryAddress.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<OrderItemResponseDTO> orderItemListToOrderItemResponseDTOList(List<OrderItem> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemResponseDTO> list1 = new ArrayList<OrderItemResponseDTO>( list.size() );
        for ( OrderItem orderItem : list ) {
            list1.add( orderItemMapper.toResponseDTO( orderItem ) );
        }

        return list1;
    }
}
