package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.RestaurantCategoryRequestDTO;
import com.utown.utown_backend.dto.response.RestaurantCategoryResponseDTO;
import com.utown.utown_backend.entity.RestaurantCategory;
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
public class RestaurantCategoryMapperImpl implements RestaurantCategoryMapper {

    @Override
    public RestaurantCategory toEntity(RestaurantCategoryRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        RestaurantCategory restaurantCategory = new RestaurantCategory();

        restaurantCategory.setName( dto.getName() );
        restaurantCategory.setImageUrl( dto.getImageUrl() );
        restaurantCategory.setPriority( dto.getPriority() );

        return restaurantCategory;
    }

    @Override
    public RestaurantCategoryResponseDTO toResponseDTO(RestaurantCategory entity) {
        if ( entity == null ) {
            return null;
        }

        RestaurantCategoryResponseDTO.RestaurantCategoryResponseDTOBuilder restaurantCategoryResponseDTO = RestaurantCategoryResponseDTO.builder();

        if ( entity.getId() != null ) {
            restaurantCategoryResponseDTO.id( entity.getId() );
        }
        restaurantCategoryResponseDTO.name( entity.getName() );
        restaurantCategoryResponseDTO.imageUrl( entity.getImageUrl() );
        restaurantCategoryResponseDTO.priority( entity.getPriority() );

        return restaurantCategoryResponseDTO.build();
    }

    @Override
    public List<RestaurantCategoryResponseDTO> toResponseList(List<RestaurantCategory> entities) {
        if ( entities == null ) {
            return null;
        }

        List<RestaurantCategoryResponseDTO> list = new ArrayList<RestaurantCategoryResponseDTO>( entities.size() );
        for ( RestaurantCategory restaurantCategory : entities ) {
            list.add( toResponseDTO( restaurantCategory ) );
        }

        return list;
    }
}
