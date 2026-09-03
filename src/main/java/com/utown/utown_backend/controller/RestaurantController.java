package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.RestaurantRequestDTO;
import com.utown.utown_backend.dto.request.RestaurantStatusUpdateDTO;
import com.utown.utown_backend.dto.response.PageResponseDTO;
import com.utown.utown_backend.dto.response.RestaurantResponseDTO;
import com.utown.utown_backend.dto.response.RestaurantStatusResponseDTO;
import com.utown.utown_backend.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Validated
public class RestaurantController {

    private final RestaurantService service;

    @Operation(summary = "Create a new restaurant",
            description = "Only users with RESTAURANT_ADMIN role can create a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant created successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    @PreAuthorize("hasRole('RESTAURANT_ADMIN')")
    public ResponseEntity<RestaurantResponseDTO> create(@Valid @RequestBody RestaurantRequestDTO dto) {
        RestaurantResponseDTO response = service.create(dto);
        URI location = URI.create("/restaurants/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get all restaurants",
            description = "Returns a paginated list of all restaurants")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of restaurants returned")
    })
    @GetMapping
    public PageResponseDTO<RestaurantResponseDTO> getAll(
            @Parameter(description = "Zero-based page index", example = "0")
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "page must be 0 or greater") int page,
            @Parameter(description = "Page size (max 100)", example = "20")
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size must be at least 1")
            @Max(value = 100, message = "size must not exceed 100") int size) {
        return service.getAll(page, size);
    }

    @Operation(summary = "Get restaurant by ID",
            description = "Retrieve details of a single restaurant by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant details returned"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @GetMapping("/{id}")
    public RestaurantResponseDTO getById(
            @Parameter(description = "Restaurant ID", example = "1")
            @PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Update a restaurant",
            description = "Only ADMIN or the restaurant owner (RESTAURANT_ADMIN) can update the restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    public RestaurantResponseDTO update(
            @Parameter(description = "Restaurant ID to update", example = "1")
            @PathVariable Long id, @Valid @RequestBody RestaurantRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Update restaurant status",
            description = "Only ADMIN or RESTAURANT_ADMIN can change the status of a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant status updated successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found"),
            @ApiResponse(responseCode = "400", description = "Invalid status update")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    public ResponseEntity<RestaurantStatusResponseDTO> updateRestaurantStatus(
            @Parameter(description = "Restaurant ID to update status", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RestaurantStatusUpdateDTO request) {

        RestaurantStatusResponseDTO response =
                service.updateRestaurantStatus(id, request);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a restaurant",
            description = "Only ADMIN or RESTAURANT_ADMIN can delete a restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurant deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied / wrong role"),
            @ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Restaurant ID to delete", example = "1")
            @PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}