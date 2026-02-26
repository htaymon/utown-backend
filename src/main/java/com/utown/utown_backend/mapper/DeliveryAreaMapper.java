package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.DeliveryAreaRequestDTO;
import com.utown.utown_backend.dto.response.DeliveryAreaResponseDTO;
import com.utown.utown_backend.entity.DeliveryArea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryAreaMapper {

    DeliveryArea toEntity(DeliveryAreaRequestDTO dto);

    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "restaurant.name", target = "restaurantName")
    DeliveryAreaResponseDTO toResponseDTO(DeliveryArea deliveryArea);

    List<DeliveryAreaResponseDTO> toResponseList(List<DeliveryArea> deliveryAreas);
}