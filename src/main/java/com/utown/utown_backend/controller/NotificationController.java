package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.NotificationRequestDTO;
import com.utown.utown_backend.dto.response.NotificationResponseDTO;
import com.utown.utown_backend.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService service;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<NotificationResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/my")
    public List<NotificationResponseDTO> getMyNotifications() {
        return service.getMyNotifications();
    }

    @GetMapping("/{id}")
    public NotificationResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<NotificationResponseDTO>  create(
            @Valid @RequestBody NotificationRequestDTO dto) {

        NotificationResponseDTO response = service.create(dto);
        URI location = URI.create("/notifications/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public NotificationResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody NotificationRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
