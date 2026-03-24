package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.enums.RestaurantStatus;
import com.utown.utown_backend.exception.CartAlreadyExistsException;
import com.utown.utown_backend.exception.RestaurantClosedException;
import com.utown.utown_backend.mapper.CartMapper;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartMapper mapper;
    private final AuthService authService;

    public CartResponseDTO create(CartRequestDTO dto) {

        User user = authService.getCurrentUser();

        log.info("CREATE_CART request: userId={}, restaurantId={}",
                user.getId(), dto.getRestaurantId());

        if (cartRepository.existsByUserId(user.getId())) {
            log.warn("CREATE_CART failed: reason=cart_already_exists, userId={}",
                    user.getId());
            throw new CartAlreadyExistsException("User already has a cart");
        }
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));
        if (restaurant.getStatus() != RestaurantStatus.OPEN) {
            log.warn("CREATE_CART failed: reason=restaurant_closed, restaurantId={}, userId={}",
                    restaurant.getId(), user.getId());
            throw new RestaurantClosedException("Restaurant is closed");
        }

        Cart cart = mapper.toEntity(dto);
        cart.setUser(user);
        cart.setRestaurant(restaurant);
        Cart saved = cartRepository.save(cart);
        log.info("CREATE_CART success: cartId={}, userId={}, restaurantId={}",
                saved.getId(), user.getId(), restaurant.getId());
        return mapper.toResponseDTO(saved);
    }

    public CartResponseDTO getById(Long id) {

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        return mapper.toResponseDTO(cart);
    }

    public CartResponseDTO getMyCart() {

        User user = authService.getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        return mapper.toResponseDTO(cart);
    }

    public void delete(Long id) {

        User user = authService.getCurrentUser();

        log.info("DELETE_CART request: cartId={}, userId={}", id, user.getId());

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        if (!cart.getUser().getId().equals(user.getId())) {
            log.warn("DELETE_CART failed: reason=access_denied, cartId={}, userId={}",
                    id, user.getId());
            throw new AccessDeniedException("You can only delete your own cart");
        }
        cartRepository.delete(cart);
        log.info("DELETE_CART success: cartId={}, userId={}", id, user.getId());
    }
}