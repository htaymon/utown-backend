package com.utown.utown_backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequestDTO {

    @NotNull(message = "Restaurant ID is required")
    private Long restaurantId;
}
