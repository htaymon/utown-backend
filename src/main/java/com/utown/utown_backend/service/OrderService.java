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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final RestaurantRepository restaurantRepository;
    private final AuthService authService;
    private final OrderMapper mapper;

    public OrderResponseDTO create(OrderRequestDTO dto) {

        User user = authService.getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));

        log.info("CREATE_ORDER request: userId={}, cartId={}, addressId={}",
                user.getId(), cart.getId(), dto.getDeliveryAddressId());

        List<CartItem> items = cart.getCartItems();
        if (items.isEmpty()) {
            log.warn("CART_EMPTY: userId={}, cartId={}", user.getId(), cart.getId());
            throw new CartEmptyException("Cart is empty. Cannot create order.");
        }

        Restaurant restaurant = cart.getRestaurant();
        if (restaurant.getStatus() == RestaurantStatus.CLOSED) {
            throw new RestaurantClosedException("Restaurant is closed");
        }

        for (CartItem item : items) {
            Dish dish = item.getDish();
            if (dish.getStatus() != DishStatus.AVAILABLE) {
                log.warn("DISH_NOT_AVAILABLE: dishId={}, userId={}",
                        dish.getId(), user.getId());
                throw new DishNotAvailableException(
                        "Dish not available: " + dish.getName());
            }
        }

        Address address = addressRepository.findById(dto.getDeliveryAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new UserAddressMismatchException("Address does not belong to the user");
        }

        double totalPrice = items
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

        List<OrderItem> orderItems = items
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

        log.info("CREATE_ORDER success: orderId={}, userId={}, totalPrice={}",
                savedOrder.getId(), user.getId(), totalPrice);

        cart.getCartItems().clear();
        cartRepository.save(cart);

        return mapper.toResponseDTO(savedOrder);
    }

    public List<OrderResponseDTO> getUserOrders(int page, int size) {

        User user = authService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUserId(user.getId(), pageable)
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

        log.info("FETCH_RESTAURANT_ORDERS: restaurantId={}, userId={}, page={}, size={}",
                restaurantId, user.getId(), page, size);

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found"));

        boolean isOwner = restaurant.getUser().getId().equals(user.getId());

        boolean isAdmin = user.getRole() != null && user.getRole().getName().equals("ADMIN");

        if (!isAdmin && !isOwner) {
            log.warn("UNAUTHORIZED_RESTAURANT_ORDER_ACCESS: restaurantId={}, userId={}",
                    restaurantId, user.getId());
            throw new AccessDeniedException("Not authorized to view these orders");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<OrderResponseDTO> result =  orderRepository.findByRestaurantId(restaurantId, pageable)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();

        log.info("FETCHED_RESTAURANT_ORDERS: restaurantId={}, count={}",
                restaurantId, result.size());

        return result;

    }

    public OrderStatusResponseDTO updateOrderStatus(Long orderId, OrderStatusUpdateDTO request) {

        User user = authService.getCurrentUser();

        log.info("UPDATE_ORDER_STATUS request: orderId={}, requestedStatus={}, userId={}",
                orderId, request.getStatus(), user.getId());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        boolean isAdmin = user.getRole() !=null && "ADMIN".equals(user.getRole().getName());
        boolean isOwner = order.getRestaurant().getUser().getId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            log.warn("UNAUTHORIZED_STATUS_UPDATE: orderId={}, userId={}",
                    orderId, user.getId());
            throw new AccessDeniedException("You are not allowed to update order status");
        }

        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.COMPLETED) {
            log.warn("INVALID_ORDER_STATUS_UPDATE: orderId={}", orderId);
            throw new InvalidOrderStatusException("Cannot change status of a completed order");
        }
        order.setStatus(request.getStatus());

        orderRepository.save(order);

        log.info("UPDATE_ORDER_STATUS success: orderId={}, newStatus={}",
                orderId, request.getStatus());

        return OrderStatusResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .build();
    }

    public OrderResponseDTO cancelOrder(Long orderId) {

        User user = authService.getCurrentUser();

        log.info("CANCEL_ORDER request: orderId={}, userId={}", orderId, user.getId());

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        try {
            checkOrderAccess(order, user);
        } catch (AccessDeniedException ex) {
            log.warn("UNAUTHORIZED_CANCEL_ORDER: orderId={}, userId={}",
                    orderId, user.getId());
            throw ex;
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            log.warn("INVALID_CANCEL_ORDER: orderId={}, status={}",
                    orderId, order.getStatus());
            throw new InvalidOrderStatusException(
                    "Only PENDING orders can be cancelled"
            );
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order saved = orderRepository.save(order);
        log.info("CANCEL_ORDER success: orderId={}", orderId);
        return mapper.toResponseDTO(saved);
    }

    private void checkOrderAccess(Order order, User user) {
        boolean isClient = order.getUser().getId().equals(user.getId());
        boolean isRestaurantOwner = order.getRestaurant().getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null && "ADMIN".equals(user.getRole().getName());

        if (!(isClient || isRestaurantOwner || isAdmin)) {
            throw new AccessDeniedException("Not authorized for this order");
        }
    }
}