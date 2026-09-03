package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.WorkScheduleRequestDTO;
import com.utown.utown_backend.dto.response.WorkScheduleResponseDTO;
import com.utown.utown_backend.service.WorkScheduleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/work-schedules")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class WorkScheduleController {

    private final WorkScheduleService service;

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PostMapping
    public ResponseEntity<WorkScheduleResponseDTO> create(@Valid @RequestBody WorkScheduleRequestDTO dto) {
        WorkScheduleResponseDTO response = service.create(dto);
        URI location = URI.create("/work-schedules/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<WorkScheduleResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public WorkScheduleResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PutMapping("/{id}")
    public WorkScheduleResponseDTO update(@PathVariable Long id, @Valid @RequestBody WorkScheduleRequestDTO dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}