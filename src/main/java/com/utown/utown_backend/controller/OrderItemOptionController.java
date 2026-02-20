package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.OrderItemOptionDTO;
import com.utown.utown_backend.service.OrderItemOptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-item-options")
public class OrderItemOptionController {

    private final OrderItemOptionService service;

    public OrderItemOptionController(OrderItemOptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<OrderItemOptionDTO> getAll() {
        return service.getAllOrderItemOptions();
    }

    @GetMapping("/{id}")
    public OrderItemOptionDTO getById(@PathVariable Long id) {
        return service.getOrderItemOptionById(id);
    }

    @PostMapping
    public OrderItemOptionDTO create(@RequestBody OrderItemOptionDTO dto) {
        return service.createOrderItemOption(dto);
    }

    @PutMapping("/{id}")
    public OrderItemOptionDTO update(@PathVariable Long id, @RequestBody OrderItemOptionDTO dto) {
        return service.updateOrderItemOption(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteOrderItemOption(id);
    }
}
