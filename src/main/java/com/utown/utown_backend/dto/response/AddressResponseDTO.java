package com.utown.utown_backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Response object representing an address")
@Getter
@AllArgsConstructor
@Builder
public class AddressResponseDTO {

    @Schema(description = "Address ID", example = "1")
    private final long id;

    @Schema(description = "Username of the address owner", example = "john")
    private final String userName;

    @Schema(description = "Street address", example = "123 Gangnam-daero")
    private final String street;

    @Schema(description = "City name", example = "Seoul")
    private final String city;

    @Schema(description = "State or province", example = "Seoul")
    private final String state;

    @Schema(description = "Postal code", example = "06018")
    private final String postalCode;
}