package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DeliveryAreaRequestDTO;
import com.utown.utown_backend.dto.response.DeliveryAreaResponseDTO;
import com.utown.utown_backend.service.DeliveryAreaService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/delivery-areas")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DeliveryAreaController {

    private final DeliveryAreaService service;

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PostMapping
    public ResponseEntity<DeliveryAreaResponseDTO> create(@Valid @RequestBody DeliveryAreaRequestDTO dto) {

        DeliveryAreaResponseDTO response = service.create(dto);
        URI location = URI.create("/delivery-areas/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<DeliveryAreaResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DeliveryAreaResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PutMapping("/{id}")
    public DeliveryAreaResponseDTO update(@PathVariable Long id,
                                          @Valid @RequestBody DeliveryAreaRequestDTO dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}