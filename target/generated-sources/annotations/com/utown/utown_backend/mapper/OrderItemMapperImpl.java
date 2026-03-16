package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.OrderItemResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.OrderItem;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-13T15:50:02+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Override
    public OrderItemResponseDTO toResponseDTO(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }

        OrderItemResponseDTO.OrderItemResponseDTOBuilder orderItemResponseDTO = OrderItemResponseDTO.builder();

        orderItemResponseDTO.dishId( orderItemDishId( orderItem ) );
        orderItemResponseDTO.dishName( orderItemDishName( orderItem ) );
        orderItemResponseDTO.id( orderItem.getId() );
        orderItemResponseDTO.quantity( orderItem.getQuantity() );
        orderItemResponseDTO.price( orderItem.getPrice() );

        return orderItemResponseDTO.build();
    }

    private Long orderItemDishId(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Dish dish = orderItem.getDish();
        if ( dish == null ) {
            return null;
        }
        Long id = dish.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String orderItemDishName(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Dish dish = orderItem.getDish();
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
