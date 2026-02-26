package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.RestaurantCategoryRequestDTO;
import com.utown.utown_backend.dto.response.RestaurantCategoryResponseDTO;
import com.utown.utown_backend.entity.RestaurantCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantCategoryMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "priority", target = "priority")
    RestaurantCategory toEntity(RestaurantCategoryRequestDTO dto);

    RestaurantCategoryResponseDTO toResponseDTO(RestaurantCategory entity);

    List<RestaurantCategoryResponseDTO> toResponseList(List<RestaurantCategory> entities);
}