package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.WorkScheduleRequestDTO;
import com.utown.utown_backend.dto.response.WorkScheduleResponseDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.entity.WorkSchedule;
import com.utown.utown_backend.mapper.WorkScheduleMapper;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.WorkScheduleRepository;
import com.utown.utown_backend.security.RestaurantAccessGuard;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WorkScheduleService {

    private final WorkScheduleRepository repository;
    private final WorkScheduleMapper mapper;
    private final RestaurantRepository restaurantRepository;
    private final AuthService authService;
    private final RestaurantAccessGuard accessGuard;

    public WorkScheduleResponseDTO create(WorkScheduleRequestDTO dto) {
        User user = authService.getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        accessGuard.check(restaurant, user);
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
        User user = authService.getCurrentUser();

        WorkSchedule ws = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkSchedule not found"));

        accessGuard.check(ws.getRestaurant(), user);

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        accessGuard.check(restaurant, user);

        ws.setRestaurant(restaurant);
        ws.setDayOfWeek(dto.getDayOfWeek());
        ws.setStartTime(dto.getStartTime());
        ws.setEndTime(dto.getEndTime());

        repository.save(ws);
        return mapper.toResponseDTO(ws);
    }

    public void delete(Long id) {
        User user = authService.getCurrentUser();

        WorkSchedule ws = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("WorkSchedule not found"));

        accessGuard.check(ws.getRestaurant(), user);

        repository.delete(ws);
    }
}