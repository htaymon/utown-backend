package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.service.AuthService;
import com.utown.utown_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<CartResponseDTO> create(@Valid @RequestBody CartRequestDTO dto) {
        CartResponseDTO response = service.create(dto);
        URI location = URI.create("/carts/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")
    public CartResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CLIENT')")
    public CartResponseDTO getMyCart() {
        return service.getMyCart();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}