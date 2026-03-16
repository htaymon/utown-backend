package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.RestaurantRequestDTO;
import com.utown.utown_backend.dto.request.RestaurantStatusUpdateDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.dto.response.RestaurantResponseDTO;
import com.utown.utown_backend.dto.response.RestaurantStatusResponseDTO;
import com.utown.utown_backend.service.OrderService;
import com.utown.utown_backend.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
public class RestaurantController {

    private final RestaurantService service;
    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> create(@Valid @RequestBody RestaurantRequestDTO dto) {
        RestaurantResponseDTO response = service.create(dto);
        URI location = URI.create("/restaurants/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public RestaurantResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public RestaurantResponseDTO update(@PathVariable Long id, @Valid @RequestBody RestaurantRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RestaurantStatusResponseDTO> updateRestaurantStatus(
            @PathVariable Long id,
            @RequestBody RestaurantStatusUpdateDTO request,
            Authentication authentication) {

        RestaurantStatusResponseDTO response =
                service.updateRestaurantStatus(id, request, authentication);

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}