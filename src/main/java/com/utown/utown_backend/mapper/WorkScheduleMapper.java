package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.WorkScheduleRequestDTO;
import com.utown.utown_backend.dto.response.WorkScheduleResponseDTO;
import com.utown.utown_backend.entity.WorkSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {

    @Mapping(source = "restaurant.id", target = "restaurantId")
    WorkScheduleResponseDTO toResponseDTO(WorkSchedule entity);

    List<WorkScheduleResponseDTO> toResponseList(List<WorkSchedule> entities);

    @Mapping(source = "restaurantId", target = "restaurant.id")
    WorkSchedule toEntity(WorkScheduleRequestDTO dto);
}