package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.OrderItemRequestDTO;
import com.utown.utown_backend.dto.request.OrderItemOptionRequestDTO;
import com.utown.utown_backend.dto.response.OrderItemOptionResponseDTO;
import com.utown.utown_backend.dto.response.OrderItemResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.Order;
import com.utown.utown_backend.entity.OrderItem;
import com.utown.utown_backend.entity.OrderItemOption;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T02:40:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class OrderItemMapperImpl implements OrderItemMapper {

    @Override
    public OrderItem toEntity(OrderItemRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        OrderItem.OrderItemBuilder orderItem = OrderItem.builder();

        orderItem.order( orderItemRequestDTOToOrder( dto ) );
        orderItem.dish( orderItemRequestDTOToDish( dto ) );
        orderItem.orderItemOptions( orderItemOptionRequestDTOListToOrderItemOptionList( dto.getOptions() ) );
        orderItem.quantity( dto.getQuantity() );
        orderItem.price( dto.getPrice() );

        return orderItem.build();
    }

    @Override
    public OrderItemResponseDTO toResponseDTO(OrderItem entity) {
        if ( entity == null ) {
            return null;
        }

        OrderItemResponseDTO.OrderItemResponseDTOBuilder orderItemResponseDTO = OrderItemResponseDTO.builder();

        orderItemResponseDTO.orderId( entityOrderId( entity ) );
        orderItemResponseDTO.dishId( entityDishId( entity ) );
        orderItemResponseDTO.dishName( entityDishName( entity ) );
        orderItemResponseDTO.options( orderItemOptionListToOrderItemOptionResponseDTOList( entity.getOrderItemOptions() ) );
        orderItemResponseDTO.id( entity.getId() );
        orderItemResponseDTO.quantity( entity.getQuantity() );
        orderItemResponseDTO.price( entity.getPrice() );

        return orderItemResponseDTO.build();
    }

    protected Order orderItemRequestDTOToOrder(OrderItemRequestDTO orderItemRequestDTO) {
        if ( orderItemRequestDTO == null ) {
            return null;
        }

        Order.OrderBuilder order = Order.builder();

        order.id( orderItemRequestDTO.getOrderId() );

        return order.build();
    }

    protected Dish orderItemRequestDTOToDish(OrderItemRequestDTO orderItemRequestDTO) {
        if ( orderItemRequestDTO == null ) {
            return null;
        }

        Dish.DishBuilder dish = Dish.builder();

        dish.id( orderItemRequestDTO.getDishId() );

        return dish.build();
    }

    protected OrderItemOption orderItemOptionRequestDTOToOrderItemOption(OrderItemOptionRequestDTO orderItemOptionRequestDTO) {
        if ( orderItemOptionRequestDTO == null ) {
            return null;
        }

        OrderItemOption.OrderItemOptionBuilder orderItemOption = OrderItemOption.builder();

        return orderItemOption.build();
    }

    protected List<OrderItemOption> orderItemOptionRequestDTOListToOrderItemOptionList(List<OrderItemOptionRequestDTO> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemOption> list1 = new ArrayList<OrderItemOption>( list.size() );
        for ( OrderItemOptionRequestDTO orderItemOptionRequestDTO : list ) {
            list1.add( orderItemOptionRequestDTOToOrderItemOption( orderItemOptionRequestDTO ) );
        }

        return list1;
    }

    private Long entityOrderId(OrderItem orderItem) {
        if ( orderItem == null ) {
            return null;
        }
        Order order = orderItem.getOrder();
        if ( order == null ) {
            return null;
        }
        Long id = order.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityDishId(OrderItem orderItem) {
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

    private String entityDishName(OrderItem orderItem) {
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

    protected OrderItemOptionResponseDTO orderItemOptionToOrderItemOptionResponseDTO(OrderItemOption orderItemOption) {
        if ( orderItemOption == null ) {
            return null;
        }

        OrderItemOptionResponseDTO.OrderItemOptionResponseDTOBuilder orderItemOptionResponseDTO = OrderItemOptionResponseDTO.builder();

        if ( orderItemOption.getId() != null ) {
            orderItemOptionResponseDTO.id( orderItemOption.getId() );
        }

        return orderItemOptionResponseDTO.build();
    }

    protected List<OrderItemOptionResponseDTO> orderItemOptionListToOrderItemOptionResponseDTOList(List<OrderItemOption> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemOptionResponseDTO> list1 = new ArrayList<OrderItemOptionResponseDTO>( list.size() );
        for ( OrderItemOption orderItemOption : list ) {
            list1.add( orderItemOptionToOrderItemOptionResponseDTO( orderItemOption ) );
        }

        return list1;
    }
}
