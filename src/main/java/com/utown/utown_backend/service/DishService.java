package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.DishRequestDTO;
import com.utown.utown_backend.dto.response.DishResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.DishCategory;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.mapper.DishMapper;
import com.utown.utown_backend.repository.DishCategoryRepository;
import com.utown.utown_backend.repository.DishRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DishService {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishCategoryRepository dishCategoryRepository;
    private final DishMapper mapper;

    public DishResponseDTO create(DishRequestDTO dto) {

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        DishCategory category = dishCategoryRepository.findById(dto.getDishCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("DishCategory not found"));

        Dish dish = Dish.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .image(dto.getImage())
                .status(dto.getStatus())
                .priority(dto.getPriority())
                .restaurant(restaurant)
                .dishCategory(category)
                .build();

        Dish savedDish = dishRepository.save(dish);

        return mapper.toResponseDTO(savedDish);
    }

    @Transactional(readOnly = true)
    public List<DishResponseDTO> getAll() {
        return mapper.toResponseList(
                dishRepository.findAll()
        );
    }

    @Transactional(readOnly = true)
    public DishResponseDTO getById(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        return mapper.toResponseDTO(dish);
    }

    public DishResponseDTO update(Long id, DishRequestDTO dto) {

        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        DishCategory category = dishCategoryRepository.findById(dto.getDishCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("DishCategory not found"));

        dish.setName(dto.getName());
        dish.setDescription(dto.getDescription());
        dish.setPrice(dto.getPrice());
        dish.setImage(dto.getImage());
        dish.setStatus(dto.getStatus());
        dish.setPriority(dto.getPriority());
        dish.setRestaurant(restaurant);
        dish.setDishCategory(category);

        return mapper.toResponseDTO(
                dishRepository.save(dish)
        );
    }

    public void delete(Long id) {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));
        dishRepository.delete(dish);
    }
}