package com.utown.utown_backend.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

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
    @DecimalMin(value = "0.0", message = "Extra price cannot be negative")
    private BigDecimal extraPrice;
}