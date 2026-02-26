package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.OrderItemOptionResponseDTO;
import com.utown.utown_backend.entity.OrderItemOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemOptionMapper {

    @Mapping(source = "orderItem.id", target = "orderItemId")
    @Mapping(source = "option.id", target = "optionId")
    OrderItemOptionResponseDTO toResponseDTO(OrderItemOption entity);

    List<OrderItemOptionResponseDTO> toResponseList(List<OrderItemOption> entities);
}
