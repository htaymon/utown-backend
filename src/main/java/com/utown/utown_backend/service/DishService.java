package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.DishDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.DishCategory;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.repository.DishCategoryRepository;
import com.utown.utown_backend.repository.DishRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishCategoryRepository dishCategoryRepository;

    public DishService(DishRepository dishRepository,
                       RestaurantRepository restaurantRepository,
                       DishCategoryRepository dishCategoryRepository) {
        this.dishRepository = dishRepository;
        this.restaurantRepository = restaurantRepository;
        this.dishCategoryRepository = dishCategoryRepository;
    }

    public List<Dish> getAllDishes() {
        return dishRepository.findAll();
    }

    public Dish getDishById(Long id) {
        return dishRepository.findById(id).orElse(null);
    }

    public Dish createDish(DishDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        DishCategory category = dishCategoryRepository.findById(dto.getDishCategoryId())
                .orElseThrow(() -> new RuntimeException("DishCategory not found"));

        Dish dish = new Dish(restaurant, category, dto.getName(), dto.getDescription(),
                dto.getPrice(), dto.getImage(), dto.getStatus(), dto.getPriority());
        return dishRepository.save(dish);
    }

    public Dish updateDish(Long id, DishDTO dto) {
        Dish dish = dishRepository.findById(id).orElse(null);
        if (dish != null) {
            Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
            DishCategory category = dishCategoryRepository.findById(dto.getDishCategoryId())
                    .orElseThrow(() -> new RuntimeException("DishCategory not found"));

            dish.setRestaurant(restaurant);
            dish.setDishCategory(category);
            dish.setName(dto.getName());
            dish.setDescription(dto.getDescription());
            dish.setPrice(dto.getPrice());
            dish.setImage(dto.getImage());
            dish.setStatus(dto.getStatus());
            dish.setPriority(dto.getPriority());

            return dishRepository.save(dish);
        }
        return null;
    }

    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }
}
