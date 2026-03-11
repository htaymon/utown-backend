package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.OrderItemOptionResponseDTO;
import com.utown.utown_backend.entity.Option;
import com.utown.utown_backend.entity.OrderItem;
import com.utown.utown_backend.entity.OrderItemOption;
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
public class OrderItemOptionMapperImpl implements OrderItemOptionMapper {

    @Override
    public OrderItemOptionResponseDTO toResponseDTO(OrderItemOption entity) {
        if ( entity == null ) {
            return null;
        }

        OrderItemOptionResponseDTO.OrderItemOptionResponseDTOBuilder orderItemOptionResponseDTO = OrderItemOptionResponseDTO.builder();

        orderItemOptionResponseDTO.orderItemId( entityOrderItemId( entity ) );
        orderItemOptionResponseDTO.optionId( entityOptionId( entity ) );
        if ( entity.getId() != null ) {
            orderItemOptionResponseDTO.id( entity.getId() );
        }

        return orderItemOptionResponseDTO.build();
    }

    @Override
    public List<OrderItemOptionResponseDTO> toResponseList(List<OrderItemOption> entities) {
        if ( entities == null ) {
            return null;
        }

        List<OrderItemOptionResponseDTO> list = new ArrayList<OrderItemOptionResponseDTO>( entities.size() );
        for ( OrderItemOption orderItemOption : entities ) {
            list.add( toResponseDTO( orderItemOption ) );
        }

        return list;
    }

    private Long entityOrderItemId(OrderItemOption orderItemOption) {
        if ( orderItemOption == null ) {
            return null;
        }
        OrderItem orderItem = orderItemOption.getOrderItem();
        if ( orderItem == null ) {
            return null;
        }
        Long id = orderItem.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long entityOptionId(OrderItemOption orderItemOption) {
        if ( orderItemOption == null ) {
            return null;
        }
        Option option = orderItemOption.getOption();
        if ( option == null ) {
            return null;
        }
        Long id = option.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
