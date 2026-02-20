package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.RestaurantDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.RestaurantCategory;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.repository.RestaurantCategoryRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantCategoryRepository categoryRepository;

    public RestaurantService(RestaurantRepository restaurantRepository, UserRepository userRepository, RestaurantCategoryRepository categoryRepository) {
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findById(id).orElse(null);
    }

    public Restaurant createRestaurant(RestaurantDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RestaurantCategory category = categoryRepository.findById(dto.getRestaurantCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Restaurant restaurant = new Restaurant(user, category, dto.getName(), dto.getDescription(), dto.getImageUrl(), dto.getMinimumOrder());
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Long id, RestaurantDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            RestaurantCategory category = categoryRepository.findById(dto.getRestaurantCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            restaurant.setUser(user);
            restaurant.setCategory(category);
            restaurant.setName(dto.getName());
            restaurant.setDescription(dto.getDescription());
            restaurant.setImageUrl(dto.getImageUrl());
            restaurant.setMinimumOrder(dto.getMinimumOrder());

            return restaurantRepository.save(restaurant);
        }
        return null;
    }

    public void deleteRestaurant(Long id) {
        restaurantRepository.deleteById(id);
    }
}
