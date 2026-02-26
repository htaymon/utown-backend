package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.RoleRequestDTO;
import com.utown.utown_backend.dto.response.RoleResponseDTO;
import com.utown.utown_backend.entity.Role;
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
public class RoleMapperImpl implements RoleMapper {

    @Override
    public RoleResponseDTO toResponseDTO(Role role) {
        if ( role == null ) {
            return null;
        }

        RoleResponseDTO.RoleResponseDTOBuilder roleResponseDTO = RoleResponseDTO.builder();

        roleResponseDTO.id( role.getId() );
        roleResponseDTO.name( role.getName() );

        return roleResponseDTO.build();
    }

    @Override
    public List<RoleResponseDTO> toResponseList(List<Role> roles) {
        if ( roles == null ) {
            return null;
        }

        List<RoleResponseDTO> list = new ArrayList<RoleResponseDTO>( roles.size() );
        for ( Role role : roles ) {
            list.add( toResponseDTO( role ) );
        }

        return list;
    }

    @Override
    public Role toEntity(RoleRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Role.RoleBuilder role = Role.builder();

        role.name( dto.getName() );

        return role.build();
    }
}
