package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.RestaurantRequestDTO;
import com.utown.utown_backend.dto.response.RestaurantResponseDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.RestaurantCategory;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.enums.RestaurantStatus;
import com.utown.utown_backend.mapper.RestaurantMapper;
import com.utown.utown_backend.repository.RestaurantCategoryRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantCategoryRepository categoryRepository;
    private final RestaurantMapper mapper;

    public RestaurantResponseDTO create(RestaurantRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RestaurantCategory category = categoryRepository.findById(dto.getRestaurantCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        RestaurantStatus status;
        try {
            status = RestaurantStatus.valueOf(dto.getStatus().toString());
        } catch (Exception e) {
            throw new RuntimeException("Invalid restaurant status");
        }

        Restaurant restaurant = Restaurant.builder()
                .user(user)
                .category(category)
                .name(dto.getName())
                .description(dto.getDescription())
                .imageUrl(dto.getImageUrl())
                .minimumOrder(dto.getMinimumOrder())
                .status(status)
                .build();

        restaurantRepository.save(restaurant);

        return mapper.toResponseDTO(restaurant);
    }

    public List<RestaurantResponseDTO> getAll() {
        List<Restaurant> restaurants = restaurantRepository.findAll();
        return mapper.toResponseList(restaurants);
    }

    public RestaurantResponseDTO getById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + id));
        return mapper.toResponseDTO(restaurant);
    }

    public RestaurantResponseDTO update(Long id, RestaurantRequestDTO dto) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setMinimumOrder(dto.getMinimumOrder());
        restaurant.setStatus(dto.getStatus());
        restaurantRepository.save(restaurant);

        return mapper.toResponseDTO(restaurant);
    }

    public void delete(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        restaurantRepository.delete(restaurant);
    }
}