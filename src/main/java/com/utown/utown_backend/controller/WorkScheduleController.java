package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.WorkScheduleRequestDTO;
import com.utown.utown_backend.dto.response.WorkScheduleResponseDTO;
import com.utown.utown_backend.service.WorkScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/work-schedules")
@RequiredArgsConstructor
public class WorkScheduleController {

    private final WorkScheduleService service;

    @PostMapping
    public WorkScheduleResponseDTO create(@RequestBody WorkScheduleRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<WorkScheduleResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public WorkScheduleResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public WorkScheduleResponseDTO update(@PathVariable Long id, @RequestBody WorkScheduleRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}