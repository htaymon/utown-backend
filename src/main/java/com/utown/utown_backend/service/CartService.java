package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import com.utown.utown_backend.entity.Cart;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.exception.CartAlreadyExistsException;
import com.utown.utown_backend.mapper.CartMapper;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartMapper mapper;
    private final AuthService authService;

    public CartResponseDTO create(CartRequestDTO dto) {

        User user = authService.getCurrentUser();
        if (cartRepository.existsByUserId(user.getId())) {
            throw new CartAlreadyExistsException("User already has a cart");
        }

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

    public CartResponseDTO getCartByUser(Long userId) {

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user"));

        return mapper.toResponseDTO(cart);
    }

    public void delete(Long id) {
        cartRepository.deleteById(id);
    }
}