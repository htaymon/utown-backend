package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.RestaurantCategoryRequestDTO;
import com.utown.utown_backend.dto.response.RestaurantCategoryResponseDTO;
import com.utown.utown_backend.service.RestaurantCategoryService;
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
@RequestMapping("/restaurant-categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RestaurantCategoryController {

    private final RestaurantCategoryService service;

    @Operation(summary = "Create a restaurant category",
            description = "Only ADMIN or RESTAURANT_ADMIN can create restaurant categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PostMapping
    public ResponseEntity<RestaurantCategoryResponseDTO> create(@Valid @RequestBody RestaurantCategoryRequestDTO dto) {
        RestaurantCategoryResponseDTO response = service.create(dto);
        URI location = URI.create("/restaurant-categories/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get all restaurant categories",
            description = "Accessible by all authenticated users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of restaurant categories returned")
    })
    @GetMapping
    public ResponseEntity<List<RestaurantCategoryResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Get restaurant category by ID",
            description = "Accessible by any authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant category details returned"),
            @ApiResponse(responseCode = "404", description = "Restaurant category not found")
    })
    @GetMapping("/{id}")
    public RestaurantCategoryResponseDTO getById(
            @Parameter(description = "Restaurant category ID", example = "1")
            @PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Update restaurant category",
            description = "Only ADMIN or RESTAURANT_ADMIN can update restaurant categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant category not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PutMapping("/{id}")
    public RestaurantCategoryResponseDTO update(
            @Parameter(description = "Restaurant category ID to update", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RestaurantCategoryRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Delete restaurant category",
            description = "Only ADMIN or RESTAURANT_ADMIN can delete restaurant categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurant category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Restaurant category not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Restaurant category ID to delete", example = "1")
            @PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}