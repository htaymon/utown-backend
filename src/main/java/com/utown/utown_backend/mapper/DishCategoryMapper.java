package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.DishCategoryRequestDTO;
import com.utown.utown_backend.dto.response.DishCategoryResponseDTO;
import com.utown.utown_backend.entity.DishCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DishCategoryMapper {

    @Mapping(source = "name", target = "name")
    @Mapping(source = "imageUrl", target = "imageUrl")
    @Mapping(source = "priority", target = "priority")
    DishCategory toEntity(DishCategoryRequestDTO dto);

    DishCategoryResponseDTO toResponseDTO(DishCategory entity);

    List<DishCategoryResponseDTO> toResponseList(List<DishCategory> entities);
}