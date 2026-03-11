package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.OrderRequestDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.entity.*;
import com.utown.utown_backend.enums.OrderStatus;
import com.utown.utown_backend.enums.RestaurantStatus;
import com.utown.utown_backend.exception.CartEmptyException;
import com.utown.utown_backend.exception.RestaurantClosedException;
import com.utown.utown_backend.mapper.OrderMapper;
import com.utown.utown_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final AuthService authService;
    private final OrderMapper mapper;

    public OrderResponseDTO create(OrderRequestDTO dto) {

        User user = authService.getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        if (items.isEmpty()) {
            throw new CartEmptyException("Cart is empty. Cannot create order.");
        }

        Restaurant restaurant = cart.getRestaurant();

        if (restaurant.getStatus() == RestaurantStatus.CLOSED) {
            throw new RestaurantClosedException("Restaurant is closed");
        }

        Address address = addressRepository.findById(dto.getDeliveryAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        double totalPrice = cart.getCartItems()
                .stream()
                .mapToDouble(item ->
                        item.getDish().getPrice() * item.getQuantity())
                .sum();

        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .deliveryAddress(address)
                .orderNo(UUID.randomUUID().toString())
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        List<OrderItem> orderItems = cart.getCartItems()
                .stream()
                .map(ci -> OrderItem.builder()
                        .order(order)
                        .dish(ci.getDish())
                        .quantity(ci.getQuantity())
                        .price(ci.getDish().getPrice())
                        .build())
                .toList();

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapper.toResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> getUserOrders(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public List<OrderResponseDTO> getRestaurantOrders(Long restaurantId) {

        return orderRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public OrderResponseDTO cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);

        return mapper.toResponseDTO(orderRepository.save(order));
    }
}