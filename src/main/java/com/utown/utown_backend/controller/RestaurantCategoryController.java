package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.RestaurantCategoryRequestDTO;
import com.utown.utown_backend.dto.response.RestaurantCategoryResponseDTO;
import com.utown.utown_backend.service.RestaurantCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant-categories")
@RequiredArgsConstructor
public class RestaurantCategoryController {

    private final RestaurantCategoryService service;

    @PostMapping
    public ResponseEntity<RestaurantCategoryResponseDTO> create(@RequestBody RestaurantCategoryRequestDTO dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<RestaurantCategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}