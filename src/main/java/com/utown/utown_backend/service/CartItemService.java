package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.CartItem;
import com.utown.utown_backend.entity.Dish;
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
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final DishRepository dishRepository;
    private final CartItemMapper mapper;

    public CartItemResponseDTO create(CartItemRequestDTO dto) {

        Cart cart = cartRepository.findById(dto.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        if (cart.getRestaurant().getStatus() != RestaurantStatus.OPEN) {
            throw new RestaurantClosedException("Restaurant is closed");
        }

        if (dish.getStatus() != DishStatus.AVAILABLE) {
            throw new DishNotAvailableException("Dish is not available");
        }

        if (!dish.getRestaurant().getId().equals(cart.getRestaurant().getId())) {
            throw new DishRestaurantMismatchException("Dish does not belong to this restaurant");
        }

        CartItem existingItem = cartItemRepository
                .findByCartIdAndDishId(dto.getCartId(), dto.getDishId())
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + dto.getQuantity());
            return mapper.toResponseDTO(cartItemRepository.save(existingItem));
        }

        CartItem item = CartItem.builder()
                .cart(cart)
                .dish(dish)
                .quantity(dto.getQuantity())
                .build();

        return mapper.toResponseDTO(cartItemRepository.save(item));
    }

    public CartItemResponseDTO update(Long id, CartItemRequestDTO dto) {

        CartItem existing = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));

        if (dto.getQuantity() <= 0) {
            cartItemRepository.delete(existing);
            return null;
        }

        existing.setQuantity(dto.getQuantity());

        return mapper.toResponseDTO(cartItemRepository.save(existing));
    }

    public void delete(Long id) {
        cartItemRepository.deleteById(id);
    }

    public List<CartItemResponseDTO> getAll() {
        return mapper.toResponseList(cartItemRepository.findAll());
    }
}