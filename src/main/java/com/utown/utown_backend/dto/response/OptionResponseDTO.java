package com.utown.utown_backend.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@Builder
public class OptionResponseDTO {

    private final long id;
    private final String name;
    private final BigDecimal extraPrice;
    private final Long dishId;
    private final String dishName;
}