package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.OrderRequestDTO;
import com.utown.utown_backend.dto.request.OrderStatusUpdateDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.dto.response.OrderStatusResponseDTO;
import com.utown.utown_backend.entity.*;
import com.utown.utown_backend.enums.DishStatus;
import com.utown.utown_backend.enums.OrderStatus;
import com.utown.utown_backend.enums.RestaurantStatus;
import com.utown.utown_backend.exception.*;
import com.utown.utown_backend.mapper.OrderMapper;
import com.utown.utown_backend.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final CartItemRepository cartItemRepository;
    private final RestaurantRepository restaurantRepository;
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

        for (CartItem item : items) {
            Dish dish = item.getDish();
            if (dish.getStatus() != DishStatus.AVAILABLE) {
                throw new DishNotAvailableException(
                        "Dish not available: " + dish.getName());
            }
        }

        Address address = addressRepository.findById(dto.getDeliveryAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UserAddressMismatchException("Address does not belong to the user");
        }

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

    public List<OrderResponseDTO> getUserOrders(Long userId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUserId(userId, pageable)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public OrderResponseDTO getOrderDetail(Long orderId) {
        User user = authService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        checkOrderAccess(order, user);

        return mapper.toResponseDTO(order);
    }

    public List<OrderResponseDTO> getRestaurantOrders(Long restaurantId,int page, int size) {

        User user = authService.getCurrentUser();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        boolean isOwner = restaurant.getUser().getId().equals(user.getId());

        boolean isAdmin = user.getRole() != null && user.getRole().getName().equals("ADMIN");

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Not authorized to view these orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByRestaurantId(restaurantId, pageable)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    public OrderStatusResponseDTO updateOrderStatus(Long orderId, OrderStatusUpdateDTO request) {
        User user = authService.getCurrentUser();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        boolean isAdmin = user.getRole().getName().equals("ADMIN");
        boolean isOwner = order.getRestaurant().getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to update order status");
        }

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.COMPLETED) {
            throw new InvalidOrderStatusException("Cannot change status of a completed order");
        }
        order.setStatus(request.getStatus());

        orderRepository.save(order);

        return OrderStatusResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .build();
    }

    public OrderResponseDTO cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        User user = authService.getCurrentUser();

        checkOrderAccess(order, user);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(
                    "Only PENDING orders can be cancelled"
            );
        }
        order.setStatus(OrderStatus.CANCELLED);
        return mapper.toResponseDTO(orderRepository.save(order));
    }

    private void checkOrderAccess(Order order, User user) {
        boolean isClient = order.getUser().getId().equals(user.getId());
        boolean isRestaurantOwner = order.getRestaurant().getUser().getId().equals(user.getId());
        boolean isAdmin = "ADMIN".equals(user.getRole().getName());

        if (!(isClient || isRestaurantOwner || isAdmin)) {
            throw new AccessDeniedException("Not authorized for this order");
        }
    }
}