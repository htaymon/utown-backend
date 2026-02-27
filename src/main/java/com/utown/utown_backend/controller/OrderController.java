package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.OrderRequestDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public OrderResponseDTO create(@RequestBody OrderRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public OrderResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public List<OrderResponseDTO> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public OrderResponseDTO update(@PathVariable Long id,
                                   @RequestBody OrderRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}