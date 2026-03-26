package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "Request object for creating or updating an address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequestDTO {

    @Schema(description = "Street address", example = "123 Gangnam-daero")
    @NotBlank(message = "Street is required")
    private String street;

    @Schema(description = "City name", example = "Seoul")
    @NotBlank(message = "City is required")
    private String city;

    @Schema(description = "State or province", example = "Seoul")
    @NotBlank(message = "State is required")
    private String state;

    @Schema(description = "Postal code", example = "06018")
    @NotBlank(message = "Postal Code is required")
    private String postalCode;
}