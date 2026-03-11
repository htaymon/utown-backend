package com.utown.utown_backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionRequestDTO {

    @NotNull(message = "Dish ID is required")
    private Long dishId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Extra Price is required")
    private Double extraPrice;
}