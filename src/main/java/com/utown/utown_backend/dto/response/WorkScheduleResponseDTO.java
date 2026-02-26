package com.utown.utown_backend.dto.response;

import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Builder
public class WorkScheduleResponseDTO {

    private Long id;
    private final Long restaurantId;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
}