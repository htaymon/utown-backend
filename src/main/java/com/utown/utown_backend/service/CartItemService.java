package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.CartItem;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.enums.DishStatus;
import com.utown.utown_backend.enums.RestaurantStatus;
import com.utown.utown_backend.exception.DishNotAvailableException;
import com.utown.utown_backend.exception.DishRestaurantMismatchException;
import com.utown.utown_backend.exception.RestaurantClosedException;
import com.utown.utown_backend.mapper.CartItemMapper;
import com.utown.utown_backend.repository.CartItemRepository;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final DishRepository dishRepository;
    private final CartItemMapper mapper;
    private final AuthService authService;

    public CartItemResponseDTO create(CartItemRequestDTO dto) {

        User user=authService.getCurrentUser();

        log.info("Add to cart: userId={}, cartId={}, dishId={}, quantity={}",
                user.getId(), dto.getCartId(), dto.getDishId(), dto.getQuantity());

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        if (cart.getRestaurant().getStatus() != RestaurantStatus.OPEN) {
            log.warn("Restaurant closed: restaurantId={}, userId={}",
                    cart.getRestaurant().getId(), user.getId());
            throw new RestaurantClosedException("Restaurant is closed");
        }

        if (dish.getStatus() != DishStatus.AVAILABLE) {
            log.warn("Dish not available: dishId={}, userId={}",
                    dish.getId(), user.getId());
            throw new DishNotAvailableException("Dish is not available");
        }

        if (!dish.getRestaurant().getId().equals(cart.getRestaurant().getId())) {
            log.warn("Dish-restaurant mismatch: dishId={}, cartId={}",
                    dish.getId(), cart.getId());
            throw new DishRestaurantMismatchException("Dish does not belong to this restaurant");
        }

        if (!cart.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized cart access: cartId={}, userId={}",
                    cart.getId(), user.getId());
            throw new AccessDeniedException("You cannot modify another user's cart");
        }
        CartItem existingItem = cartItemRepository
                .findByCartIdAndDishId(dto.getCartId(), dto.getDishId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            log.info("Cart item updated (increase quantity): cartItemId={}, newQuantity={}",
                    existingItem.getId(), existingItem.getQuantity());
            return mapper.toResponseDTO(cartItemRepository.save(existingItem));
        }

        CartItem item = CartItem.builder()
                .cart(cart)
                .dish(dish)
                .quantity(dto.getQuantity())
                .build();

        CartItem saved = cartItemRepository.save(item);
        log.info("Cart item created: cartItemId={}, cartId={}, dishId={}",
                saved.getId(), cart.getId(), dish.getId());
        return mapper.toResponseDTO(saved);

    }

    public CartItemResponseDTO update(Long id, CartItemRequestDTO dto) {

        User user = authService.getCurrentUser();

        log.info("Update cart item: cartItemId={}, userId={}, quantity={}",
                id, user.getId(), dto.getQuantity());

        CartItem existing = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        if (cart.getRestaurant().getStatus() != RestaurantStatus.OPEN) {
            log.warn("Restaurant closed: restaurantId={}, userId={}",
                    cart.getRestaurant().getId(), user.getId());
            throw new RestaurantClosedException("Restaurant is closed");
        }

        if (dish.getStatus() != DishStatus.AVAILABLE) {
            log.warn("Dish not available: dishId={}, userId={}",
                    dish.getId(), user.getId());
            throw new DishNotAvailableException("Dish is not available");
        }

        if (!dish.getRestaurant().getId().equals(cart.getRestaurant().getId())) {
            log.warn("Dish restaurant mismatch: dishId={}, cartId={}",
                    dish.getId(), cart.getId());
            throw new DishRestaurantMismatchException("Dish does not belong to this restaurant");
        }

        if (!cart.getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized cart update: cartId={}, userId={}",
                    cart.getId(), user.getId());
            throw new AccessDeniedException("You cannot modify another user's cart");
        }

        existing.setQuantity(dto.getQuantity());

        CartItem saved = cartItemRepository.save(existing);

        log.info("Cart item updated: cartItemId={}, newQuantity={}",
                saved.getId(), saved.getQuantity());

        return mapper.toResponseDTO(saved);
    }

    public void delete(Long id) {
        User user=authService.getCurrentUser();
        log.info("Delete cart item: cartItemId={}, userId={}", id, user.getId());

        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));

        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            log.warn("Unauthorized delete attempt: cartItemId={}, userId={}",
                    id, user.getId());
            throw new AccessDeniedException("You cannot modify another user's cart");
        }

        cartItemRepository.deleteById(id);
        log.info("Cart item deleted: cartItemId={}", id);
    }

    public List<CartItemResponseDTO> getAll() {
        User user=authService.getCurrentUser();
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));
        return mapper.toResponseList(cart.getCartItems());
    }
}