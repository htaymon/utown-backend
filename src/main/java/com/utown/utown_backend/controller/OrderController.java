package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.OrderDTO;
import com.utown.utown_backend.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) { this.service = service; }

    @GetMapping
    public List<OrderDTO> getAll() { return service.getAllOrders(); }

    @GetMapping("/{id}")
    public OrderDTO getById(@PathVariable Long id) { return service.getOrderById(id); }

    @PostMapping
    public OrderDTO create(@RequestBody OrderDTO dto) { return service.createOrder(dto); }

    @PutMapping("/{id}")
    public OrderDTO update(@PathVariable Long id, @RequestBody OrderDTO dto) { return service.updateOrder(id, dto); }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.deleteOrder(id); }
}

