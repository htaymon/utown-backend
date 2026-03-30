package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.UserResponseDTO;
import com.utown.utown_backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role.name", target = "roleName")
    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseList(List<User> users);

}