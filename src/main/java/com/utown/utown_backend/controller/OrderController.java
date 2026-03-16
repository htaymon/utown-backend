package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.OrderRequestDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.repository.UserRepository;
import com.utown.utown_backend.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO dto) {
        OrderResponseDTO response = orderService.create(dto);
        URI location = URI.create("/orders/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/my")
    public List<OrderResponseDTO> getMyOrders() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return orderService.getUserOrders(user.getId());
    }

    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    @GetMapping("/restaurant/{restaurantId}")
    public List<OrderResponseDTO> getRestaurantOrders(
            @PathVariable Long restaurantId) {

        return orderService.getRestaurantOrders(restaurantId);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }
}