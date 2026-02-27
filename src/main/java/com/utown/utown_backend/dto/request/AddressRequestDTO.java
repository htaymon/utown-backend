package com.utown.utown_backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {

    private Long userId;
    private String street;
    private String city;
    private String state;
    private String postalCode;
}