package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.OptionRequestDTO;
import com.utown.utown_backend.dto.response.OptionResponseDTO;
import com.utown.utown_backend.entity.Option;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    Option toEntity(OptionRequestDTO dto);

    @Mapping(source = "dish.id", target = "dishId")
    @Mapping(source = "dish.name", target = "dishName")
    OptionResponseDTO toResponseDTO(Option option);

    List<OptionResponseDTO> toResponseList(List<Option> options);
}