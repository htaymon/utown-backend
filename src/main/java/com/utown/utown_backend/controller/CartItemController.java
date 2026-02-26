package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.CartItemRequestDTO;
import com.utown.utown_backend.dto.response.CartItemResponseDTO;
import com.utown.utown_backend.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart-items")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService service;

    @PostMapping
    public CartItemResponseDTO create(@RequestBody CartItemRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CartItemResponseDTO update(@PathVariable Long id,
                                      @RequestBody CartItemRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping
    public List<CartItemResponseDTO> getAll() {
        return service.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}