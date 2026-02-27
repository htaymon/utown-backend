package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

    private final long id;
    private final String userName;
    private final String street;
    private final String city;
    private final String state;
    private final String postalCode;
}