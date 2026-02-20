package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.RestaurantCategoryDTO;
import com.utown.utown_backend.entity.RestaurantCategory;
import com.utown.utown_backend.service.RestaurantCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurant-categories")
public class RestaurantCategoryController {

    private final RestaurantCategoryService service;

    public RestaurantCategoryController(RestaurantCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<RestaurantCategory> getAllCategories() {
        return service.getAllCategories();
    }

    @GetMapping("/{id}")
    public RestaurantCategory getCategoryById(@PathVariable Long id) {
        return service.getCategoryById(id);
    }

    @PostMapping
    public RestaurantCategory createCategory(@RequestBody RestaurantCategoryDTO dto) {
        return service.createCategory(dto);
    }

    @PutMapping("/{id}")
    public RestaurantCategory updateCategory(@PathVariable Long id, @RequestBody RestaurantCategoryDTO dto) {
        return service.updateCategory(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        service.deleteCategory(id);
    }
}
