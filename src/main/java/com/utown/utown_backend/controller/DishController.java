package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DishRequestDTO;
import com.utown.utown_backend.dto.response.DishResponseDTO;
import com.utown.utown_backend.service.DishService;
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
@RequestMapping("/dishes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DishController {

    private final DishService service;

    @Operation(summary = "Create a new dish",
            description = "Only ADMIN or RESTAURANT_ADMIN can create a dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Dish created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "404", description = "Restaurant or category not found"),
            @ApiResponse(responseCode = "400", description = "Validation error / missing fields")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PostMapping
    public ResponseEntity<DishResponseDTO> create(@Valid @RequestBody DishRequestDTO dto) {
        DishResponseDTO response = service.create(dto);
        URI location = URI.create("/dishes/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get all dishes",
            description = "Returns a list of all dishes, accessible to all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of dishes returned successfully")
    })
    @GetMapping
    public List<DishResponseDTO> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get dish by ID",
            description = "Returns details of a dish by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish details returned successfully"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    @GetMapping("/{id}")
    public DishResponseDTO getById(
            @Parameter(description = "Dish ID", example = "1")
            @PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Update a dish",
            description = "Only ADMIN or RESTAURANT_ADMIN can update a dish")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dish updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "404", description = "Dish or category not found"),
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @PutMapping("/{id}")
    public DishResponseDTO update(
            @Parameter(description = "Dish ID to update", example = "1")
            @PathVariable Long id,
            @RequestBody DishRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Delete a dish",
            description = "Only ADMIN or RESTAURANT_ADMIN can delete a dish. Dish will be hidden.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dish deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "404", description = "Dish not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Dish ID to delete", example = "1")
            @PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}