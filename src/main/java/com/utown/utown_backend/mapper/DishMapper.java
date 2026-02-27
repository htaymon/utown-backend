package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.DishRequestDTO;
import com.utown.utown_backend.dto.response.DishResponseDTO;
import com.utown.utown_backend.entity.Dish;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DishMapper {

    Dish toEntity(DishRequestDTO dto);

    @Mapping(source = "restaurant.id", target = "restaurantId")
    @Mapping(source = "restaurant.name", target = "restaurantName")
    @Mapping(source = "dishCategory.id", target = "dishCategoryId")
    @Mapping(source = "dishCategory.name", target = "dishCategoryName")
    DishResponseDTO toResponseDTO(Dish dish);

    List<DishResponseDTO> toResponseList(List<Dish> dishes);
}