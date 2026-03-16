package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DishRequestDTO;
import com.utown.utown_backend.dto.response.DishResponseDTO;
import com.utown.utown_backend.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
public class DishController {

    private final DishService service;

    @GetMapping
    public List<DishResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DishResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<DishResponseDTO> create(@RequestBody DishRequestDTO dto) {
        DishResponseDTO response = service.create(dto);
        URI location = URI.create("/dishes/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public DishResponseDTO update(
            @PathVariable Long id,
            @RequestBody DishRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}