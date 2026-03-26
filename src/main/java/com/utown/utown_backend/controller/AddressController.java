package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.AddressRequestDTO;
import com.utown.utown_backend.dto.response.AddressResponseDTO;
import com.utown.utown_backend.service.AddressService;
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
@RequestMapping("/addresses")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AddressController {

    private final AddressService service;

    @Operation(summary = "Create a new address",
            description = "CLIENT can create a new delivery address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@Valid @RequestBody AddressRequestDTO dto) {
        AddressResponseDTO response = service.create(dto);
        URI location = URI.create("/addresses/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "Get all addresses for current user",
            description = "Returns a list of addresses belonging to the logged-in CLIENT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of addresses returned"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public List<AddressResponseDTO> getAll() {
        return service.getAll();
    }

    @Operation(summary = "Get address by ID",
            description = "Returns details of a specific address owned by the CLIENT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address returned"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public AddressResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @Operation(summary = "Update an address",
            description = "CLIENT can update an existing address they own")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping("/{id}")
    public AddressResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDTO dto) {
        return service.update(id, dto);
    }

    @Operation(summary = "Delete an address",
            description = "CLIENT can delete an address they own")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}