package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.RoleRequestDTO;
import com.utown.utown_backend.dto.response.RoleResponseDTO;
import com.utown.utown_backend.entity.Role;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponseDTO toResponseDTO(Role role);

    List<RoleResponseDTO> toResponseList(List<Role> roles);

    Role toEntity(RoleRequestDTO dto);
}