package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.RestaurantRequestDTO;
import com.utown.utown_backend.dto.request.RestaurantStatusUpdateDTO;
import com.utown.utown_backend.dto.response.PageResponseDTO;
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
import com.utown.utown_backend.security.RestaurantAccessGuard;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantCategoryRepository categoryRepository;
    private final RestaurantMapper mapper;
    private final AuthService authService;
    private final RestaurantAccessGuard accessGuard;

    public RestaurantResponseDTO create(RestaurantRequestDTO dto) {

        User user = authService.getCurrentUser();

        log.info("CREATE_RESTAURANT request: userId={}, categoryId={}",
                user.getId(), dto.getRestaurantCategoryId());

        RestaurantCategory category = categoryRepository.findById(dto.getRestaurantCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        RestaurantStatus status;
        try {
            status = RestaurantStatus.valueOf(dto.getStatus().toString());
        } catch (IllegalArgumentException e) {
            log.warn("CREATE_RESTAURANT failed: reason=invalid_status, userId={}",
                    user.getId());
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

        Restaurant saved = restaurantRepository.save(restaurant);

        log.info("CREATE_RESTAURANT success: restaurantId={}, userId={}",
                saved.getId(), user.getId());

        return mapper.toResponseDTO(saved);
    }

    public PageResponseDTO<RestaurantResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RestaurantResponseDTO> result = restaurantRepository.findAll(pageable)
                .map(mapper::toResponseDTO);
        return PageResponseDTO.from(result);
    }

    public RestaurantResponseDTO getById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found with id: " + id));
        return mapper.toResponseDTO(restaurant);
    }

    public RestaurantResponseDTO update(Long id, RestaurantRequestDTO dto) {

        User user = authService.getCurrentUser();
        log.info("UPDATE_RESTAURANT request: restaurantId={}, userId={}",
                id, user.getId());
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        accessGuard.check(restaurant, user);

        restaurant.setName(dto.getName());
        restaurant.setDescription(dto.getDescription());
        restaurant.setMinimumOrder(dto.getMinimumOrder());
        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("UPDATE_RESTAURANT success: restaurantId={}, userId={}",
                id, user.getId());
        return mapper.toResponseDTO(saved);
    }

    public RestaurantStatusResponseDTO updateRestaurantStatus(
            Long restaurantId,
            RestaurantStatusUpdateDTO request) {

        User user = authService.getCurrentUser();
        log.info("UPDATE_RESTAURANT_STATUS request: restaurantId={}, userId={}, status={}",
                restaurantId, user.getId(), request.getStatus());

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        accessGuard.check(restaurant, user);

        restaurant.setStatus(request.getStatus());

        Restaurant saved = restaurantRepository.save(restaurant);

        log.info("UPDATE_RESTAURANT_STATUS success: restaurantId={}, status={}",
                restaurantId, request.getStatus());

        return RestaurantStatusResponseDTO.builder()
                .restaurantId(saved.getId())
                .status(saved.getStatus())
                .build();
    }

    public void delete(Long id) {
        User user = authService.getCurrentUser();

        log.info("DELETE_RESTAURANT request: restaurantId={}, userId={}",
                id, user.getId());

        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        accessGuard.check(restaurant, user);

        restaurantRepository.delete(restaurant);
        log.info("DELETE_RESTAURANT success: restaurantId={}, userId={}",
                id, user.getId());
    }
}