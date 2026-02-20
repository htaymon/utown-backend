package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.DishCategoryDTO;
import com.utown.utown_backend.entity.DishCategory;
import com.utown.utown_backend.service.DishCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish-categories")
public class DishCategoryController {

    private final DishCategoryService service;

    public DishCategoryController(DishCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<DishCategory> getAllCategories() {
        return service.getAllCategories();
    }

    @GetMapping("/{id}")
    public DishCategory getCategoryById(@PathVariable Long id) {
        return service.getCategoryById(id);
    }

    @PostMapping
    public DishCategory createCategory(@RequestBody DishCategoryDTO dto) {
        return service.createCategory(dto);
    }

    @PutMapping("/{id}")
    public DishCategory updateCategory(@PathVariable Long id, @RequestBody DishCategoryDTO dto) {
        return service.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
    }
}
