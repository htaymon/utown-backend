package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DishRequestDTO;
import com.utown.utown_backend.dto.response.DishResponseDTO;
import com.utown.utown_backend.service.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
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
    public DishResponseDTO create(@RequestBody DishRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public DishResponseDTO update(
            @PathVariable Long id,
            @RequestBody DishRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}