package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.OrderDTO;
import com.utown.utown_backend.dto.OrderItemDTO;
import com.utown.utown_backend.dto.OrderItemOptionDTO;
import com.utown.utown_backend.entity.Order;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.repository.OrderRepository;
import com.utown.utown_backend.repository.UserRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;

    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public OrderDTO createOrder(OrderDTO dto) {
        User user = userRepository.findById(dto.getUserId()).orElse(null);
        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId()).orElse(null);

        Order order = new Order(
                user,
                restaurant,
                dto.getOrderNo(),
                dto.getStatus(),
                dto.getTotalPrice(),
                dto.getCookingTime(),
                LocalDateTime.now()
        );

        Order saved = orderRepository.save(order);
        return convertToDTO(saved);
    }

    public OrderDTO updateOrder(Long id, OrderDTO dto) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(dto.getStatus());
            order.setTotalPrice(dto.getTotalPrice());
            order.setCookingTime(dto.getCookingTime());
            Order saved = orderRepository.save(order);
            return convertToDTO(saved);
        }
        return null;
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        dto.setRestaurantId(order.getRestaurant() != null ? order.getRestaurant().getRestaurantId() : null);
        dto.setOrderNo(order.getOrderNo());
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());
        dto.setCookingTime(order.getCookingTime());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getOrderItems() != null) {
            dto.setOrderItems(order.getOrderItems()
                    .stream()
                    .map(item -> {
                        OrderItemDTO itemDTO = new OrderItemDTO();
                        itemDTO.setOrderItemId(item.getOrderItemId());
                        itemDTO.setOrderId(order.getOrderId());
                        itemDTO.setDishId(item.getDish() != null ? item.getDish().getDishId() : null);
                        itemDTO.setQuantity(item.getQuantity());
                        itemDTO.setPrice(item.getPrice());

                        if (item.getOrderItemOptions() != null) {
                            itemDTO.setOrderItemOptions(item.getOrderItemOptions()
                                    .stream()
                                    .map(opt -> {
                                        OrderItemOptionDTO optDTO = new OrderItemOptionDTO();
                                        optDTO.setOrderItemOptionId(opt.getOrderItemOptionId());
                                        optDTO.setOrderItemId(item.getOrderItemId());
                                        optDTO.setOptionId(opt.getOption() != null ? opt.getOption().getOptionId() : null);
                                        return optDTO;
                                    }).collect(Collectors.toList()));
                        }

                        return itemDTO;
                    }).collect(Collectors.toList()));
        }

        return dto;
    }
}
