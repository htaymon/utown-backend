package com.utown.utown_backend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemOptionRequestDTO {

    private Long orderItemId;
    private Long optionId;
}