package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.DishDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.service.DishService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dishes")
public class DishController {

    private final DishService service;

    public DishController(DishService service) {
        this.service = service;
    }

    @GetMapping
    public List<Dish> getAllDishes() {
        return service.getAllDishes();
    }

    @GetMapping("/{id}")
    public Dish getDishById(@PathVariable Long id) {
        return service.getDishById(id);
    }

    @PostMapping
    public Dish createDish(@RequestBody DishDTO dto) {
        return service.createDish(dto);
    }

    @PutMapping("/{id}")
    public Dish updateDish(@PathVariable Long id, @RequestBody DishDTO dto) {
        return service.updateDish(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteDish(@PathVariable Long id) {
        service.deleteDish(id);
    }
}
