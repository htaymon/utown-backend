package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.OrderItemDTO;
import com.utown.utown_backend.service.OrderItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderItemDTO> getAll() {
        return service.getAllOrderItems();
    }

    @GetMapping("/{id}")
    public OrderItemDTO getById(@PathVariable Long id) {
        return service.getOrderItemById(id);
    }

    @PostMapping
    public OrderItemDTO create(@RequestBody OrderItemDTO dto) {
        return service.createOrderItem(dto);
    }

    @PutMapping("/{id}")
    public OrderItemDTO update(@PathVariable Long id, @RequestBody OrderItemDTO dto) {
        return service.updateOrderItem(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteOrderItem(id);
    }
}
