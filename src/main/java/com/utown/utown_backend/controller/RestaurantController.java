package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.RestaurantRequestDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.dto.response.RestaurantResponseDTO;
import com.utown.utown_backend.service.OrderService;
import com.utown.utown_backend.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;
    private final OrderService orderService;

    @PostMapping
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

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}