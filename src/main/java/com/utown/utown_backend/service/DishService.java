package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.DishRequestDTO;
import com.utown.utown_backend.dto.response.DishResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.DishCategory;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.enums.DishStatus;
import com.utown.utown_backend.mapper.DishMapper;
import com.utown.utown_backend.repository.DishCategoryRepository;
import com.utown.utown_backend.repository.DishRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DishService {

    private final DishRepository dishRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishCategoryRepository dishCategoryRepository;
    private final DishMapper mapper;
    private final AuthService authService;

    public DishResponseDTO create(DishRequestDTO dto) {

        User user = authService.getCurrentUser();
        log.info("CREATE_DISH request: userId={}, restaurantId={}, categoryId={}",
                user.getId(), dto.getRestaurantId(), dto.getDishCategoryId());

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        checkRestaurantAccess(restaurant, user);

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

        Dish saved = dishRepository.save(dish);

        log.info("CREATE_DISH success: dishId={}, restaurantId={}",
                saved.getId(), restaurant.getId());

        return mapper.toResponseDTO(saved);
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

        User user = authService.getCurrentUser();
        log.info("UPDATE_DISH request: dishId={}, userId={}", id, user.getId());
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        checkRestaurantAccess(dish.getRestaurant(), user);

        DishCategory category = dishCategoryRepository.findById(dto.getDishCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("DishCategory not found"));

        dish.setName(dto.getName());
        dish.setDescription(dto.getDescription());
        dish.setPrice(dto.getPrice());
        dish.setImage(dto.getImage());
        dish.setStatus(dto.getStatus());
        dish.setPriority(dto.getPriority());
        dish.setDishCategory(category);

        Dish saved = dishRepository.save(dish);
        log.info("UPDATE_DISH success: dishId={}, userId={}", id, user.getId());
        return mapper.toResponseDTO(saved);
    }

    public void delete(Long id) {
        User user = authService.getCurrentUser();
        log.info("DELETE_DISH request: dishId={}, userId={}", id, user.getId());
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));
        checkRestaurantAccess(dish.getRestaurant(), user);
        dish.setStatus(DishStatus.HIDDEN);
        dishRepository.save(dish);
        log.info("DELETE_DISH success: dishId={}, userId={}", id, user.getId());
    }

    private void checkRestaurantAccess(Restaurant restaurant, User user) {
        boolean isOwner = restaurant.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null && "ADMIN".equals(user.getRole().getName());

        if (!isOwner && !isAdmin) {
            log.warn("RESTAURANT_ACCESS_DENIED: restaurantId={}, userId={}",
                    restaurant.getId(), user.getId());
            throw new AccessDeniedException("Access denied");
        }
    }
}