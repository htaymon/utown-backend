package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.RestaurantCategoryRequestDTO;
import com.utown.utown_backend.dto.response.RestaurantCategoryResponseDTO;
import com.utown.utown_backend.service.RestaurantCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/restaurant-categories")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
public class RestaurantCategoryController {

    private final RestaurantCategoryService service;

    @PostMapping
    public ResponseEntity<RestaurantCategoryResponseDTO> create(@Valid @RequestBody RestaurantCategoryRequestDTO dto) {
        RestaurantCategoryResponseDTO response = service.create(dto);
        URI location = URI.create("/restaurant-categories/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantCategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}