package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.CartItem;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.mapper.CartItemMapper;
import com.utown.utown_backend.repository.CartItemRepository;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

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

        CartItem item = mapper.toEntity(dto);
        item.setCart(cart);
        item.setDish(dish);

        return mapper.toResponseDTO(cartItemRepository.save(item));
    }

    public CartItemResponseDTO update(Long id, CartItemRequestDTO dto) {

        CartItem existing = cartItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("CartItem not found"));

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