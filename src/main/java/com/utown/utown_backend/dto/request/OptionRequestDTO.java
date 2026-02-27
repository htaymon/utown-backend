package com.utown.utown_backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionRequestDTO {

    private Long dishId;
    private String name;
    private Double extraPrice;
}