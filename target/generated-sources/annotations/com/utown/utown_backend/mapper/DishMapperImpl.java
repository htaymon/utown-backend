package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.DishRequestDTO;
import com.utown.utown_backend.dto.response.DishResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.DishCategory;
import com.utown.utown_backend.entity.Restaurant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-03T10:52:23+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class DishMapperImpl implements DishMapper {

    @Override
    public Dish toEntity(DishRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Dish.DishBuilder dish = Dish.builder();

        dish.name( dto.getName() );
        dish.description( dto.getDescription() );
        dish.price( dto.getPrice() );
        dish.image( dto.getImage() );
        dish.status( dto.getStatus() );
        dish.priority( dto.getPriority() );

        return dish.build();
    }

    @Override
    public DishResponseDTO toResponseDTO(Dish dish) {
        if ( dish == null ) {
            return null;
        }

        DishResponseDTO.DishResponseDTOBuilder dishResponseDTO = DishResponseDTO.builder();

        dishResponseDTO.restaurantId( dishRestaurantId( dish ) );
        dishResponseDTO.restaurantName( dishRestaurantName( dish ) );
        dishResponseDTO.dishCategoryId( dishDishCategoryId( dish ) );
        dishResponseDTO.dishCategoryName( dishDishCategoryName( dish ) );
        dishResponseDTO.id( dish.getId() );
        dishResponseDTO.name( dish.getName() );
        dishResponseDTO.description( dish.getDescription() );
        dishResponseDTO.price( dish.getPrice() );
        dishResponseDTO.image( dish.getImage() );
        dishResponseDTO.status( dish.getStatus() );
        dishResponseDTO.priority( dish.getPriority() );

        return dishResponseDTO.build();
    }

    @Override
    public List<DishResponseDTO> toResponseList(List<Dish> dishes) {
        if ( dishes == null ) {
            return null;
        }

        List<DishResponseDTO> list = new ArrayList<DishResponseDTO>( dishes.size() );
        for ( Dish dish : dishes ) {
            list.add( toResponseDTO( dish ) );
        }

        return list;
    }

    private Long dishRestaurantId(Dish dish) {
        if ( dish == null ) {
            return null;
        }
        Restaurant restaurant = dish.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        Long id = restaurant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String dishRestaurantName(Dish dish) {
        if ( dish == null ) {
            return null;
        }
        Restaurant restaurant = dish.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        String name = restaurant.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long dishDishCategoryId(Dish dish) {
        if ( dish == null ) {
            return null;
        }
        DishCategory dishCategory = dish.getDishCategory();
        if ( dishCategory == null ) {
            return null;
        }
        Long id = dishCategory.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String dishDishCategoryName(Dish dish) {
        if ( dish == null ) {
            return null;
        }
        DishCategory dishCategory = dish.getDishCategory();
        if ( dishCategory == null ) {
            return null;
        }
        String name = dishCategory.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
