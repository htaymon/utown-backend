package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/cart-items")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartItemController {

    private final CartItemService service;

    @Operation(summary = "Add item to cart",
            description = "Only CLIENT role can add a dish to their cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cart item created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "404", description = "Cart or Dish not found"),
            @ApiResponse(responseCode = "400", description = "Dish unavailable or mismatch with cart restaurant")
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CartItemResponseDTO> create(@Valid @RequestBody CartItemRequestDTO dto) {
        CartItemResponseDTO response = service.create(dto);
        URI location = URI.create("/cart-items/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Update cart item",
            description = "Only CLIENT role can update their own cart item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart item updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart item, cart, or dish not found"),
            @ApiResponse(responseCode = "400", description = "Dish unavailable or mismatch with cart restaurant")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public CartItemResponseDTO update(@PathVariable Long id,
                                      @Valid @RequestBody CartItemRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Get all items in user's cart",
            description = "Only CLIENT role can retrieve their own cart items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of cart items returned"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public List<CartItemResponseDTO> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Delete cart item",
            description = "Only CLIENT role can delete their own cart item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart item deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart item not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}