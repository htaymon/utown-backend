package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.AddressResponseDTO;
import com.utown.utown_backend.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(source = "user.name", target = "userName")
    AddressResponseDTO toResponseDTO(Address address);

    List<AddressResponseDTO> toResponseList(List<Address> addresses);
}