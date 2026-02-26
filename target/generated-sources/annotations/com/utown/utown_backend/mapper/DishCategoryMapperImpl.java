package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.DishCategoryRequestDTO;
import com.utown.utown_backend.dto.response.DishCategoryResponseDTO;
import com.utown.utown_backend.entity.DishCategory;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T02:40:04+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class DishCategoryMapperImpl implements DishCategoryMapper {

    @Override
    public DishCategory toEntity(DishCategoryRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DishCategory dishCategory = new DishCategory();

        dishCategory.setName( dto.getName() );
        dishCategory.setImageUrl( dto.getImageUrl() );
        dishCategory.setPriority( dto.getPriority() );

        return dishCategory;
    }

    @Override
    public DishCategoryResponseDTO toResponseDTO(DishCategory entity) {
        if ( entity == null ) {
            return null;
        }

        DishCategoryResponseDTO.DishCategoryResponseDTOBuilder dishCategoryResponseDTO = DishCategoryResponseDTO.builder();

        dishCategoryResponseDTO.id( entity.getId() );
        dishCategoryResponseDTO.name( entity.getName() );
        dishCategoryResponseDTO.imageUrl( entity.getImageUrl() );
        dishCategoryResponseDTO.priority( entity.getPriority() );

        return dishCategoryResponseDTO.build();
    }

    @Override
    public List<DishCategoryResponseDTO> toResponseList(List<DishCategory> entities) {
        if ( entities == null ) {
            return null;
        }

        List<DishCategoryResponseDTO> list = new ArrayList<DishCategoryResponseDTO>( entities.size() );
        for ( DishCategory dishCategory : entities ) {
            list.add( toResponseDTO( dishCategory ) );
        }

        return list;
    }
}
