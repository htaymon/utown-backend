package com.utown.utown_backend.dto.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class OrderItemOptionResponseDTO {

    private final long id;
    private final Long orderItemId;
    private final Long optionId;
}