package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.OptionRequestDTO;
import com.utown.utown_backend.dto.response.OptionResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.Option;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-02-27T02:40:03+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class OptionMapperImpl implements OptionMapper {

    @Override
    public Option toEntity(OptionRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Option.OptionBuilder option = Option.builder();

        option.name( dto.getName() );
        option.extraPrice( dto.getExtraPrice() );

        return option.build();
    }

    @Override
    public OptionResponseDTO toResponseDTO(Option option) {
        if ( option == null ) {
            return null;
        }

        OptionResponseDTO.OptionResponseDTOBuilder optionResponseDTO = OptionResponseDTO.builder();

        optionResponseDTO.dishId( optionDishId( option ) );
        optionResponseDTO.dishName( optionDishName( option ) );
        if ( option.getId() != null ) {
            optionResponseDTO.id( option.getId() );
        }
        optionResponseDTO.name( option.getName() );
        optionResponseDTO.extraPrice( option.getExtraPrice() );

        return optionResponseDTO.build();
    }

    @Override
    public List<OptionResponseDTO> toResponseList(List<Option> options) {
        if ( options == null ) {
            return null;
        }

        List<OptionResponseDTO> list = new ArrayList<OptionResponseDTO>( options.size() );
        for ( Option option : options ) {
            list.add( toResponseDTO( option ) );
        }

        return list;
    }

    private Long optionDishId(Option option) {
        if ( option == null ) {
            return null;
        }
        Dish dish = option.getDish();
        if ( dish == null ) {
            return null;
        }
        Long id = dish.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String optionDishName(Option option) {
        if ( option == null ) {
            return null;
        }
        Dish dish = option.getDish();
        if ( dish == null ) {
            return null;
        }
        String name = dish.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
