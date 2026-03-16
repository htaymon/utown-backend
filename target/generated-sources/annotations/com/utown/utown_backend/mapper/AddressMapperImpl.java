package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.response.AddressResponseDTO;
import com.utown.utown_backend.entity.Address;
import com.utown.utown_backend.entity.User;
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
public class AddressMapperImpl implements AddressMapper {

    @Override
    public AddressResponseDTO toResponseDTO(Address address) {
        if ( address == null ) {
            return null;
        }

        AddressResponseDTO.AddressResponseDTOBuilder addressResponseDTO = AddressResponseDTO.builder();

        addressResponseDTO.userName( addressUserName( address ) );
        if ( address.getId() != null ) {
            addressResponseDTO.id( address.getId() );
        }
        addressResponseDTO.street( address.getStreet() );
        addressResponseDTO.city( address.getCity() );
        addressResponseDTO.state( address.getState() );
        addressResponseDTO.postalCode( address.getPostalCode() );

        return addressResponseDTO.build();
    }

    @Override
    public List<AddressResponseDTO> toResponseList(List<Address> addresses) {
        if ( addresses == null ) {
            return null;
        }

        List<AddressResponseDTO> list = new ArrayList<AddressResponseDTO>( addresses.size() );
        for ( Address address : addresses ) {
            list.add( toResponseDTO( address ) );
        }

        return list;
    }

    private String addressUserName(Address address) {
        if ( address == null ) {
            return null;
        }
        User user = address.getUser();
        if ( user == null ) {
            return null;
        }
        String name = user.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }
}
