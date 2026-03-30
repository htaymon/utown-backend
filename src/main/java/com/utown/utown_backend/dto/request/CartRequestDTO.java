package com.utown.utown_backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Schema(description = "Request object for creating a cart")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDTO {

    @Schema(description = "Restaurant ID for the cart", example = "1")
    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
}
