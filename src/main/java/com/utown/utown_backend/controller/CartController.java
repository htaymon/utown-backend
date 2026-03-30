package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import com.utown.utown_backend.service.CartService;
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

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService service;

    @Operation(summary = "Create a new cart",
            description = "Only CLIENT role can create a cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cart created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CartResponseDTO> create(@Valid @RequestBody CartRequestDTO dto) {
        CartResponseDTO response = service.create(dto);
        URI location = URI.create("/carts/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get cart by ID",
            description = "ADMIN or RESTAURANT_ADMIN can retrieve any cart by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cart details returned"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    public CartResponseDTO getById(
            @Parameter(description = "Cart ID", example = "1")
            @PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Get current user's cart",
            description = "Only CLIENT role can retrieve their own cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's cart returned"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    public CartResponseDTO getMyCart() {
        return service.getMyCart();
    }

    @Operation(summary = "Delete a cart",
            description = "Only CLIENT role can delete their own cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cart deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Cart not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Cart ID to delete", example = "1")
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}