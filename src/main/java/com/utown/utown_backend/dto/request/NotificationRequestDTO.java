package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.NotificationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {

    private Long userId;
    private String message;
    private NotificationStatus status;
}