package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.service.CartItemService;
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
public class CartItemController {

    private final CartItemService service;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CartItemResponseDTO> create(@Valid @RequestBody CartItemRequestDTO dto) {
        CartItemResponseDTO response = service.create(dto);
        URI location = URI.create("/cart-items/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public CartItemResponseDTO update(@PathVariable Long id,
                                      @Valid @RequestBody CartItemRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping
    @PreAuthorize("hasRole('CLIENT')")
    public List<CartItemResponseDTO> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}