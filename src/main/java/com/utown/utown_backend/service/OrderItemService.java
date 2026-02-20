package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.OrderItemDTO;
import com.utown.utown_backend.entity.Dish;
import com.utown.utown_backend.entity.Order;
import com.utown.utown_backend.entity.OrderItem;
import com.utown.utown_backend.repository.DishRepository;
import com.utown.utown_backend.repository.OrderItemRepository;
import com.utown.utown_backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    private final OrderItemRepository itemRepository;
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;

    public OrderItemService(OrderItemRepository itemRepository,
                            OrderRepository orderRepository,
                            DishRepository dishRepository) {
        this.itemRepository = itemRepository;
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
    }

    public List<OrderItemDTO> getAllOrderItems() {
        return itemRepository.findAll()
                .stream()
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setOrderItemId(item.getOrderItemId());
                    dto.setOrderId(item.getOrder() != null ? item.getOrder().getOrderId() : null);
                    dto.setDishId(item.getDish() != null ? item.getDish().getDishId() : null);
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    return dto;
                })
                .toList();
    }

    public OrderItemDTO getOrderItemById(Long id) {
        return itemRepository.findById(id)
                .map(item -> {
                    OrderItemDTO dto = new OrderItemDTO();
                    dto.setOrderItemId(item.getOrderItemId());
                    dto.setOrderId(item.getOrder() != null ? item.getOrder().getOrderId() : null);
                    dto.setDishId(item.getDish() != null ? item.getDish().getDishId() : null);
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    return dto;
                })
                .orElse(null);
    }

    public OrderItemDTO createOrderItem(OrderItemDTO dto) {
        Order order = orderRepository.findById(dto.getOrderId()).orElse(null);
        Dish dish = dishRepository.findById(dto.getDishId()).orElse(null);

        OrderItem item = new OrderItem(order, dish, dto.getQuantity(), dto.getPrice());
        OrderItem saved = itemRepository.save(item);

        dto.setOrderItemId(saved.getOrderItemId());
        return dto;
    }

    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO dto) {
        OrderItem item = itemRepository.findById(id).orElse(null);
        if (item != null) {
            item.setQuantity(dto.getQuantity());
            item.setPrice(dto.getPrice());
            Order order = orderRepository.findById(dto.getOrderId()).orElse(null);
            Dish dish = dishRepository.findById(dto.getDishId()).orElse(null);
            item.setOrder(order);
            item.setDish(dish);
            itemRepository.save(item);
            dto.setOrderItemId(item.getOrderItemId());
            return dto;
        }
        return null;
    }

    public void deleteOrderItem(Long id) {
        itemRepository.deleteById(id);
    }
}
