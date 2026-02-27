package com.utown.utown_backend.dto.response;

import com.utown.utown_backend.enums.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class NotificationResponseDTO {

    private final Long id;
    private final Long userId;
    private final String message;
    private final NotificationStatus status;
}