package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.OrderRequestDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.entity.Address;
import com.utown.utown_backend.entity.Order;
import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.mapper.OrderMapper;
import com.utown.utown_backend.repository.AddressRepository;
import com.utown.utown_backend.repository.OrderRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import com.utown.utown_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AddressRepository addressRepository;
    private final OrderMapper mapper;

    public OrderResponseDTO create(OrderRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Restaurant restaurant = restaurantRepository.findById(dto.getRestaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        Address address = addressRepository.findById(dto.getDeliveryAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        Order order = mapper.toEntity(dto);

        order.setUser(user);
        order.setRestaurant(restaurant);
        order.setDeliveryAddress(address);
        order.setOrderNo(UUID.randomUUID().toString());

        return mapper.toResponseDTO(orderRepository.save(order));
    }

    public OrderResponseDTO getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        return mapper.toResponseDTO(order);
    }

    public List<OrderResponseDTO> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public OrderResponseDTO update(Long id, OrderRequestDTO dto) {

        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        existing.setStatus(dto.getStatus());
        existing.setTotalPrice(dto.getTotalPrice());
        existing.setCookingTime(dto.getCookingTime());

        return mapper.toResponseDTO(orderRepository.save(existing));
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }
}