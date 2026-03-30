package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.RestaurantResponseDTO;
import com.utown.utown_backend.entity.Restaurant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "userName")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    RestaurantResponseDTO toResponseDTO(Restaurant entity);

    List<RestaurantResponseDTO> toResponseList(List<Restaurant> entities);
}