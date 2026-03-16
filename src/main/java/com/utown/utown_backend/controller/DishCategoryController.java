package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DishCategoryRequestDTO;
import com.utown.utown_backend.dto.response.DishCategoryResponseDTO;
import com.utown.utown_backend.service.DishCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/dish-categories")
@RequiredArgsConstructor
public class DishCategoryController {

    private final DishCategoryService service;

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PostMapping
    public ResponseEntity<DishCategoryResponseDTO> create(@Valid @RequestBody DishCategoryRequestDTO dto) {
        DishCategoryResponseDTO response = service.create(dto);
        URI location = URI.create("/dish-categories/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public List<DishCategoryResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DishCategoryResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PutMapping("/{id}")
    public DishCategoryResponseDTO update(@PathVariable Long id,
                                          @Valid @RequestBody DishCategoryRequestDTO dto) {
        return service.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}