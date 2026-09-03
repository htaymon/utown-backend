package com.utown.utown_backend.dto.request;

import com.utown.utown_backend.enums.NotificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequestDTO {

    @NotBlank(message = "Message is required")
    private String message;

    @NotNull(message = "Status is required")
    private NotificationStatus status;
}