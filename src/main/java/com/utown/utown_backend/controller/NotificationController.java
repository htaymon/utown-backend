package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.NotificationRequestDTO;
import com.utown.utown_backend.dto.response.NotificationResponseDTO;
import com.utown.utown_backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @GetMapping
    public List<NotificationResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public NotificationResponseDTO create(
            @RequestBody NotificationRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public NotificationResponseDTO update(
            @PathVariable Long id,
            @RequestBody NotificationRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}