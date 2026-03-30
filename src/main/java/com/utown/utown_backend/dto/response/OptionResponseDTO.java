package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class OptionResponseDTO {

    private final long id;
    private final String name;
    private final Double extraPrice;
    private final Long dishId;
    private final String dishName;
}