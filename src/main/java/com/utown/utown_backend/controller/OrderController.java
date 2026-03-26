package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.OrderRequestDTO;
import com.utown.utown_backend.dto.request.OrderStatusUpdateDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.dto.response.OrderStatusResponseDTO;
import com.utown.utown_backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order",
            description = "Only CLIENT role can create orders from their cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "404", description = "Cart or Address not found"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "400", description = "Cart is empty or dish not available")
    })
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<OrderResponseDTO> create(@Valid @RequestBody OrderRequestDTO dto) {
        OrderResponseDTO response = orderService.create(dto);
        URI location = URI.create("/orders/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get current user's orders",
            description = "Only CLIENT role can view their own orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of user's orders returned"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/my")
    public List<OrderResponseDTO> getMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return orderService.getUserOrders(page, size);
    }

    @Operation(summary = "Get order details",
            description = "CLIENT can view their own order, RESTAURANT_ADMIN and ADMIN can view any order for their restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order details returned"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PreAuthorize("hasAnyRole('CLIENT','RESTAURANT_ADMIN','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDTO> getOrder(
            @Parameter(description = "Order ID", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id));
    }

    @Operation(summary = "Get orders for a restaurant",
            description = "Only RESTAURANT_ADMIN or ADMIN can view orders for a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of restaurant orders returned"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PreAuthorize("hasAnyRole('RESTAURANT_ADMIN','ADMIN')")
    @GetMapping("/restaurant/{restaurantId}")
    public List<OrderResponseDTO> getRestaurantOrders(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return orderService.getRestaurantOrders(restaurantId,page,size);
    }

    @Operation(summary = "Cancel an order",
            description = "CLIENT can cancel their own PENDING orders.ADMIN and RESTAURANT_ADMIN can cancel any order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "400", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PreAuthorize("hasAnyRole('CLIENT','RESTAURANT_ADMIN','ADMIN')")
    @PutMapping("/{id}/cancel")
    public OrderResponseDTO cancelOrder(
            @Parameter(description = "Order ID to cancel", example = "1")
            @PathVariable Long id) {
        return orderService.cancelOrder(id);
    }

    @Operation(summary = "Update order status",
            description = "Only RESTAURANT_ADMIN or ADMIN can update order status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status change")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderStatusResponseDTO> updateStatus(
            @Parameter(description = "Order ID to update status", example = "1")
            @PathVariable Long id,
            @RequestBody OrderStatusUpdateDTO request) {

        return ResponseEntity.ok(orderService.updateOrderStatus(id,request));
    }
}