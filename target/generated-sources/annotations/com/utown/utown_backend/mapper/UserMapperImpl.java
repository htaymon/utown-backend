package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.UserResponseDTO;
import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.entity.User;
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
public class UserMapperImpl implements UserMapper {

    @Override
    public UserResponseDTO toResponseDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponseDTO.UserResponseDTOBuilder userResponseDTO = UserResponseDTO.builder();

        userResponseDTO.roleName( userRoleName( user ) );
        if ( user.getId() != null ) {
            userResponseDTO.id( user.getId() );
        }
        userResponseDTO.name( user.getName() );
        userResponseDTO.email( user.getEmail() );
        userResponseDTO.phoneNumber( user.getPhoneNumber() );

        return userResponseDTO.build();
    }

    @Override
    public List<UserResponseDTO> toResponseList(List<User> users) {
        if ( users == null ) {
            return null;
        }

        List<UserResponseDTO> list = new ArrayList<UserResponseDTO>( users.size() );
        for ( User user : users ) {
            list.add( toResponseDTO( user ) );
        }

        return list;
    }

    private String userRoleName(User user) {
        if ( user == null ) {
            return null;
        }
        Role role = user.getRole();
        if ( role == null ) {
            return null;
        }
        String name = role.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
