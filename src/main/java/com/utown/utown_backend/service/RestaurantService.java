package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.RestaurantRequestDTO;
import com.utown.utown_backend.dto.request.RestaurantStatusUpdateDTO;
import com.utown.utown_backend.dto.response.RestaurantResponseDTO;
import com.utown.utown_backend.dto.response.RestaurantStatusResponseDTO;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.RestaurantCategory;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.enums.RestaurantStatus;
import com.utown.utown_backend.exception.InvalidRestaurantStatusException;
import com.utown.utown_backend.mapper.RestaurantMapper;
import com.utown.utown_backend.repository.RestaurantCategoryRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final RestaurantCategoryRepository categoryRepository;
    private final RestaurantMapper mapper;
    private final AuthService authService;

    public RestaurantResponseDTO create(RestaurantRequestDTO dto) {

        User user = authService.getCurrentUser();

        RestaurantCategory category = categoryRepository.findById(dto.getRestaurantCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        RestaurantStatus status;
        try {
            status = RestaurantStatus.valueOf(dto.getStatus().toString());
        } catch (IllegalArgumentException e) {
            throw new InvalidRestaurantStatusException("Invalid restaurant status");
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
        restaurantRepository.save(restaurant);

        return mapper.toResponseDTO(restaurant);
    }

    public RestaurantStatusResponseDTO updateRestaurantStatus(
            Long restaurantId,
            RestaurantStatusUpdateDTO request,
            Authentication authentication) {

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isAdmin = user.getRole().getName().equals("ADMIN");
        boolean isOwner = restaurant.getUser().getId().equals(user.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to update restaurant status");
        }

        restaurant.setStatus(request.getStatus());

        restaurantRepository.save(restaurant);

        return RestaurantStatusResponseDTO.builder()
                .restaurantId(restaurant.getId())
                .status(restaurant.getStatus())
                .build();
    }

    public void delete(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        restaurantRepository.delete(restaurant);
    }
}