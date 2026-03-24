package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.OrderRequestDTO;
import com.utown.utown_backend.dto.request.OrderStatusUpdateDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.dto.response.OrderStatusResponseDTO;
import com.utown.utown_backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO dto) {
        OrderResponseDTO response = orderService.create(dto);
        URI location = URI.create("/orders/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my")
    public List<OrderResponseDTO> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return orderService.getUserOrders(page, size);
    }

    @PreAuthorize("hasAnyRole('CLIENT','RESTAURANT_ADMIN','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id));
    }

    @PreAuthorize("hasAnyRole('RESTAURANT_ADMIN','ADMIN')")
    @GetMapping("/restaurant/{restaurantId}")
    public List<OrderResponseDTO> getRestaurantOrders(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return orderService.getRestaurantOrders(restaurantId,page,size);
    }

    @PreAuthorize("hasAnyRole('CLIENT','RESTAURANT_ADMIN','ADMIN')")
    @PutMapping("/{id}/cancel")
    public OrderResponseDTO cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderStatusResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO request) {

        return ResponseEntity.ok(orderService.updateOrderStatus(id,request));
    }
}