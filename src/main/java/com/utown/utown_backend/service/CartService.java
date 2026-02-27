package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.mapper.CartMapper;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartMapper mapper;

    public CartResponseDTO create(CartRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Cart cart = mapper.toEntity(dto);
        cart.setUser(user);
        cart.setRestaurant(restaurant);

        return mapper.toResponseDTO(cartRepository.save(cart));
    }

    public CartResponseDTO getById(Long id) {
        return mapper.toResponseDTO(
                cartRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Cart not found"))
        );
    }

    public List<CartResponseDTO> getAll() {
        return mapper.toResponseList(cartRepository.findAll());
    }

    public void delete(Long id) {
        cartRepository.deleteById(id);
    }
}