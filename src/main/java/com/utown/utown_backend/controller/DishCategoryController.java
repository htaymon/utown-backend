package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DishCategoryRequestDTO;
import com.utown.utown_backend.dto.response.DishCategoryResponseDTO;
import com.utown.utown_backend.service.DishCategoryService;
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
@RequestMapping("/dish-categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DishCategoryController {

    private final DishCategoryService service;

    @Operation(summary = "Create a new dish category",
            description = "Only ADMIN or RESTAURANT_ADMIN can create dish categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PostMapping
    public ResponseEntity<DishCategoryResponseDTO> create(@Valid @RequestBody DishCategoryRequestDTO dto) {
        DishCategoryResponseDTO response = service.create(dto);
        URI location = URI.create("/dish-categories/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get all dish categories",
            description = "Accessible by any authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of dish categories returned")
    })
    @GetMapping
    public List<DishCategoryResponseDTO> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get dish category by ID",
            description = "Accessible by any authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish category details returned"),
            @ApiResponse(responseCode = "404", description = "Dish category not found")
    })
    @GetMapping("/{id}")
    public DishCategoryResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Update dish category",
            description = "Only ADMIN or RESTAURANT_ADMIN can update dish categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Dish category not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PutMapping("/{id}")
    public DishCategoryResponseDTO update(@PathVariable Long id,
                                          @Valid @RequestBody DishCategoryRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Delete dish category",
            description = "Only ADMIN or RESTAURANT_ADMIN can delete dish categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dish category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Dish category not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}