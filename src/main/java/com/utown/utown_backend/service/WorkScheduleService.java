package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.WorkScheduleDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.WorkSchedule;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.WorkScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkScheduleService {

    private final WorkScheduleRepository repository;
    private final RestaurantRepository restaurantRepository;

    public WorkScheduleService(WorkScheduleRepository repository, RestaurantRepository restaurantRepository) {
        this.repository = repository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<WorkSchedule> getAllSchedules() {
        return repository.findAll();
    }

    public WorkSchedule getScheduleById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public WorkSchedule createSchedule(WorkScheduleDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        WorkSchedule schedule = new WorkSchedule(restaurant, dto.getDayOfWeek(), dto.getStartTime(), dto.getEndTime());
        return repository.save(schedule);
    }

    public WorkSchedule updateSchedule(Long id, WorkScheduleDTO dto) {
        WorkSchedule schedule = repository.findById(id).orElse(null);
        if (schedule != null) {
            Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));

            schedule.setRestaurant(restaurant);
            schedule.setDayOfWeek(dto.getDayOfWeek());
            schedule.setStartTime(dto.getStartTime());
            schedule.setEndTime(dto.getEndTime());
            return repository.save(schedule);
        }
        return null;
    }

    public void deleteSchedule(Long id) {
        repository.deleteById(id);
    }
}
