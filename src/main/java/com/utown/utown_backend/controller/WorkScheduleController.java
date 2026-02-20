package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.WorkScheduleDTO;
import com.utown.utown_backend.entity.WorkSchedule;
import com.utown.utown_backend.service.WorkScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-schedules")
public class WorkScheduleController {

    private final WorkScheduleService service;

    public WorkScheduleController(WorkScheduleService service) {
        this.service = service;
    }

    @GetMapping
    public List<WorkSchedule> getAllSchedules() {
        return service.getAllSchedules();
    }

    @GetMapping("/{id}")
    public WorkSchedule getScheduleById(@PathVariable Long id) {
        return service.getScheduleById(id);
    }

    @PostMapping
    public WorkSchedule createSchedule(@RequestBody WorkScheduleDTO dto) {
        return service.createSchedule(dto);
    }

    @PutMapping("/{id}")
    public WorkSchedule updateSchedule(@PathVariable Long id, @RequestBody WorkScheduleDTO dto) {
        return service.updateSchedule(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        service.deleteSchedule(id);
    }
}
