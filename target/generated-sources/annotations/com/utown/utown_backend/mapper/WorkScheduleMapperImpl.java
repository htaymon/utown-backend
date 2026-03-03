package com.utown.utown_backend.mapper;

import com.utown.utown_backend.dto.request.WorkScheduleRequestDTO;
import com.utown.utown_backend.dto.response.WorkScheduleResponseDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.WorkSchedule;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-03T10:52:23+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.8 (Oracle Corporation)"
)
@Component
public class WorkScheduleMapperImpl implements WorkScheduleMapper {

    @Override
    public WorkScheduleResponseDTO toResponseDTO(WorkSchedule entity) {
        if ( entity == null ) {
            return null;
        }

        WorkScheduleResponseDTO.WorkScheduleResponseDTOBuilder workScheduleResponseDTO = WorkScheduleResponseDTO.builder();

        workScheduleResponseDTO.restaurantId( entityRestaurantId( entity ) );
        workScheduleResponseDTO.id( entity.getId() );
        workScheduleResponseDTO.dayOfWeek( entity.getDayOfWeek() );
        workScheduleResponseDTO.startTime( entity.getStartTime() );
        workScheduleResponseDTO.endTime( entity.getEndTime() );

        return workScheduleResponseDTO.build();
    }

    @Override
    public List<WorkScheduleResponseDTO> toResponseList(List<WorkSchedule> entities) {
        if ( entities == null ) {
            return null;
        }

        List<WorkScheduleResponseDTO> list = new ArrayList<WorkScheduleResponseDTO>( entities.size() );
        for ( WorkSchedule workSchedule : entities ) {
            list.add( toResponseDTO( workSchedule ) );
        }

        return list;
    }

    @Override
    public WorkSchedule toEntity(WorkScheduleRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        WorkSchedule.WorkScheduleBuilder workSchedule = WorkSchedule.builder();

        workSchedule.restaurant( workScheduleRequestDTOToRestaurant( dto ) );
        workSchedule.dayOfWeek( dto.getDayOfWeek() );
        workSchedule.startTime( dto.getStartTime() );
        workSchedule.endTime( dto.getEndTime() );

        return workSchedule.build();
    }

    private Long entityRestaurantId(WorkSchedule workSchedule) {
        if ( workSchedule == null ) {
            return null;
        }
        Restaurant restaurant = workSchedule.getRestaurant();
        if ( restaurant == null ) {
            return null;
        }
        Long id = restaurant.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected Restaurant workScheduleRequestDTOToRestaurant(WorkScheduleRequestDTO workScheduleRequestDTO) {
        if ( workScheduleRequestDTO == null ) {
            return null;
        }

        Restaurant.RestaurantBuilder restaurant = Restaurant.builder();

        restaurant.id( workScheduleRequestDTO.getRestaurantId() );

        return restaurant.build();
    }
}
