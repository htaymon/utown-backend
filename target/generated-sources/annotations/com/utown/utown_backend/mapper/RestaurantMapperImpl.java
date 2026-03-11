package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.RestaurantResponseDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.RestaurantCategory;
import com.utown.utown_backend.entity.User;
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
public class RestaurantMapperImpl implements RestaurantMapper {

    @Override
    public RestaurantResponseDTO toResponseDTO(Restaurant entity) {
        if ( entity == null ) {
            return null;
        }

        RestaurantResponseDTO.RestaurantResponseDTOBuilder restaurantResponseDTO = RestaurantResponseDTO.builder();

        Long id = entityUserId( entity );
        if ( id != null ) {
            restaurantResponseDTO.userId( id );
        }
        restaurantResponseDTO.userName( entityUserName( entity ) );
        restaurantResponseDTO.categoryId( entityCategoryId( entity ) );
        restaurantResponseDTO.categoryName( entityCategoryName( entity ) );
        if ( entity.getId() != null ) {
            restaurantResponseDTO.id( entity.getId() );
        }
        restaurantResponseDTO.name( entity.getName() );
        restaurantResponseDTO.description( entity.getDescription() );
        restaurantResponseDTO.imageUrl( entity.getImageUrl() );
        restaurantResponseDTO.minimumOrder( entity.getMinimumOrder() );
        restaurantResponseDTO.status( entity.getStatus() );

        return restaurantResponseDTO.build();
    }

    @Override
    public List<RestaurantResponseDTO> toResponseList(List<Restaurant> entities) {
        if ( entities == null ) {
            return null;
        }

        List<RestaurantResponseDTO> list = new ArrayList<RestaurantResponseDTO>( entities.size() );
        for ( Restaurant restaurant : entities ) {
            list.add( toResponseDTO( restaurant ) );
        }

        return list;
    }

    private Long entityUserId(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }
        User user = restaurant.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityUserName(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }
        User user = restaurant.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long entityCategoryId(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }
        RestaurantCategory category = restaurant.getCategory();
        if ( category == null ) {
            return null;
        }
        Long id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String entityCategoryName(Restaurant restaurant) {
        if ( restaurant == null ) {
            return null;
        }
        RestaurantCategory category = restaurant.getCategory();
        if ( category == null ) {
            return null;
        }
        String name = category.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
