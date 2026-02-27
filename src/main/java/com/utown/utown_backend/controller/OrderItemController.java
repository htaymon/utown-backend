package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.OrderItemRequestDTO;
import com.utown.utown_backend.dto.response.OrderItemResponseDTO;
import com.utown.utown_backend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService service;

    @PostMapping
    public OrderItemResponseDTO create(@RequestBody OrderItemRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public OrderItemResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<OrderItemResponseDTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public OrderItemResponseDTO update(@PathVariable Long id,
                                       @RequestBody OrderItemRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}