package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.CartRequestDTO;
import com.utown.utown_backend.dto.response.CartResponseDTO;
import com.utown.utown_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CLIENT')")
public class CartController {

    private final CartService service;

    @PostMapping
    public ResponseEntity<CartResponseDTO> create(@Valid @RequestBody CartRequestDTO dto) {
        CartResponseDTO response = service.create(dto);
        URI location = URI.create("/carts/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public CartResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<CartResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/user/{userId}")
    public CartResponseDTO getCartByUser(@PathVariable Long userId) {
        return service.getCartByUser(userId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}