package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.RestaurantStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Response object representing restaurant status")
@Getter
@Builder
public class RestaurantStatusResponseDTO {

    @Schema(description = "Restaurant ID", example = "1")
    private Long restaurantId;

    @Schema(description = "Current restaurant status", example = "OPEN")
    private RestaurantStatus status;
}
