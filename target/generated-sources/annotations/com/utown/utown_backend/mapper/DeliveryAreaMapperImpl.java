package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.DeliveryAreaRequestDTO;
import com.utown.utown_backend.dto.response.DeliveryAreaResponseDTO;
import com.utown.utown_backend.entity.DeliveryArea;
import com.utown.utown_backend.entity.Restaurant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-13T15:50:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class DeliveryAreaMapperImpl implements DeliveryAreaMapper {

    @Override
    public DeliveryArea toEntity(DeliveryAreaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DeliveryArea.DeliveryAreaBuilder deliveryArea = DeliveryArea.builder();

        deliveryArea.city( dto.getCity() );
        deliveryArea.name( dto.getName() );

        return deliveryArea.build();
    }

    @Override
    public DeliveryAreaResponseDTO toResponseDTO(DeliveryArea deliveryArea) {
        if ( deliveryArea == null ) {
            return null;
        }

        DeliveryAreaResponseDTO.DeliveryAreaResponseDTOBuilder deliveryAreaResponseDTO = DeliveryAreaResponseDTO.builder();

        deliveryAreaResponseDTO.restaurantId( deliveryAreaRestaurantId( deliveryArea ) );
        deliveryAreaResponseDTO.restaurantName( deliveryAreaRestaurantName( deliveryArea ) );
        deliveryAreaResponseDTO.id( deliveryArea.getId() );
        deliveryAreaResponseDTO.city( deliveryArea.getCity() );
        deliveryAreaResponseDTO.name( deliveryArea.getName() );

        return deliveryAreaResponseDTO.build();
    }

    @Override
    public List<DeliveryAreaResponseDTO> toResponseList(List<DeliveryArea> deliveryAreas) {
        if ( deliveryAreas == null ) {
            return null;
        }

        List<DeliveryAreaResponseDTO> list = new ArrayList<DeliveryAreaResponseDTO>( deliveryAreas.size() );
        for ( DeliveryArea deliveryArea : deliveryAreas ) {
            list.add( toResponseDTO( deliveryArea ) );
        }

        return list;
    }

    private Long deliveryAreaRestaurantId(DeliveryArea deliveryArea) {
        if ( deliveryArea == null ) {
            return null;
        }
        Restaurant restaurant = deliveryArea.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        Long id = restaurant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String deliveryAreaRestaurantName(DeliveryArea deliveryArea) {
        if ( deliveryArea == null ) {
            return null;
        }
        Restaurant restaurant = deliveryArea.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        String name = restaurant.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
