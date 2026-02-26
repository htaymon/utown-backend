package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DishCategoryRequestDTO;
import com.utown.utown_backend.dto.response.DishCategoryResponseDTO;
import com.utown.utown_backend.service.DishCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish-categories")
@RequiredArgsConstructor
public class DishCategoryController {

    private final DishCategoryService service;

    @PostMapping
    public DishCategoryResponseDTO create(@RequestBody DishCategoryRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<DishCategoryResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DishCategoryResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public DishCategoryResponseDTO update(@PathVariable Long id,
                                          @RequestBody DishCategoryRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}