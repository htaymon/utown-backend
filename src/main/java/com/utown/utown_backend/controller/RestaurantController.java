package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.RestaurantDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.service.RestaurantService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @GetMapping
    public List<Restaurant> getAllRestaurants() {
        return service.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public Restaurant getRestaurantById(@PathVariable Long id) {
        return service.getRestaurantById(id);
    }

    @PostMapping
    public Restaurant createRestaurant(@RequestBody RestaurantDTO dto) {
        return service.createRestaurant(dto);
    }

    @PutMapping("/{id}")
    public Restaurant updateRestaurant(@PathVariable Long id, @RequestBody RestaurantDTO dto) {
        return service.updateRestaurant(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteRestaurant(@PathVariable Long id) {
        service.deleteRestaurant(id);
    }
}
