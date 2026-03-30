package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.RestaurantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Request object for updating restaurant status")
@Getter
@Setter
public class RestaurantStatusUpdateDTO {

    @Schema(
            description = "New restaurant status (OPEN or CLOSED)",
            example = "OPEN"
    )
    private RestaurantStatus status;
}
