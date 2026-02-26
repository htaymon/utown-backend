package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.OrderItemRequestDTO;
import com.utown.utown_backend.dto.response.OrderItemResponseDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.Order;
import com.utown.utown_backend.entity.OrderItem;
import com.utown.utown_backend.mapper.OrderItemMapper;
import com.utown.utown_backend.repository.DishRepository;
import com.utown.utown_backend.repository.OrderItemRepository;
import com.utown.utown_backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderItemMapper mapper;

    public OrderItemResponseDTO create(OrderItemRequestDTO dto) {

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        Dish dish = dishRepository.findById(dto.getDishId())
                .orElseThrow(() -> new EntityNotFoundException("Dish not found"));

        OrderItem entity = mapper.toEntity(dto);

        entity.setOrder(order);
        entity.setDish(dish);

        return mapper.toResponseDTO(
                orderItemRepository.save(entity)
        );
    }

    public OrderItemResponseDTO getById(Long id) {
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found"));

        return mapper.toResponseDTO(item);
    }

    public List<OrderItemResponseDTO> getAll() {
        return orderItemRepository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderItemResponseDTO update(Long id, OrderItemRequestDTO dto) {

        OrderItem existing = orderItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderItem not found"));

        existing.setQuantity(dto.getQuantity());
        existing.setPrice(dto.getPrice());

        return mapper.toResponseDTO(
                orderItemRepository.save(existing)
        );
    }

    public void delete(Long id) {
        orderItemRepository.deleteById(id);
    }
}