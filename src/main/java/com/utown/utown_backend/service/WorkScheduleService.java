package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.WorkScheduleRequestDTO;
import com.utown.utown_backend.dto.response.WorkScheduleResponseDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.WorkSchedule;
import com.utown.utown_backend.mapper.WorkScheduleMapper;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.WorkScheduleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository repository;
    private final WorkScheduleMapper mapper;
    private final RestaurantRepository restaurantRepository;

    public WorkScheduleResponseDTO create(WorkScheduleRequestDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        WorkSchedule entity = mapper.toEntity(dto);
        entity.setRestaurant(restaurant);
        repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    public List<WorkScheduleResponseDTO> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    public WorkScheduleResponseDTO getById(Long id) {
        WorkSchedule ws = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkSchedule not found"));
        return mapper.toResponseDTO(ws);
    }

    public WorkScheduleResponseDTO update(Long id, WorkScheduleRequestDTO dto) {
        WorkSchedule ws = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkSchedule not found"));

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        ws.setRestaurant(restaurant);
        ws.setDayOfWeek(dto.getDayOfWeek());
        ws.setStartTime(dto.getStartTime());
        ws.setEndTime(dto.getEndTime());

        repository.save(ws);
        return mapper.toResponseDTO(ws);
    }
    
    public void delete(Long id) {
        WorkSchedule ws = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkSchedule not found"));
        repository.delete(ws);
    }
}