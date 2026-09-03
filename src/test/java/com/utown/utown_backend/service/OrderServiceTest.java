package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.OrderRequestDTO;
import com.utown.utown_backend.dto.request.OrderStatusUpdateDTO;
import com.utown.utown_backend.dto.response.OrderResponseDTO;
import com.utown.utown_backend.entity.*;
import com.utown.utown_backend.enums.DishStatus;
import com.utown.utown_backend.enums.OrderStatus;
import com.utown.utown_backend.enums.RestaurantStatus;
import com.utown.utown_backend.exception.*;
import com.utown.utown_backend.mapper.OrderMapper;
import com.utown.utown_backend.repository.AddressRepository;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.repository.OrderRepository;
import com.utown.utown_backend.repository.RestaurantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private AuthService authService;
    @Mock
    private OrderMapper mapper;

    @InjectMocks
    private OrderService orderService;

    private User client;
    private Restaurant restaurant;
    private Dish dish;
    private Address address;
    private Cart cart;

    @BeforeEach
    void setUp() {
        Role clientRole = Role.builder().id(1L).name("CLIENT").build();
        client = User.builder().id(10L).name("Jane").email("jane@utown.dev").role(clientRole).build();

        User owner = User.builder().id(20L).name("Owner").role(Role.builder().id(2L).name("RESTAURANT_ADMIN").build()).build();
        restaurant = Restaurant.builder().id(1L).user(owner).status(RestaurantStatus.OPEN).minimumOrder(5.0).build();

        dish = Dish.builder().id(100L).restaurant(restaurant).name("Bulgogi").price(new BigDecimal("12.99")).status(DishStatus.AVAILABLE).build();

        address = Address.builder().id(5L).user(client).street("Main St").city("Seoul").state("Seoul").postalCode("00000").build();

        CartItem item = CartItem.builder().id(1L).dish(dish).quantity(2).build();
        List<CartItem> items = new ArrayList<>();
        items.add(item);
        cart = Cart.builder().id(7L).user(client).restaurant(restaurant).cartItems(items).build();

        lenientStubMapper();
    }

    private void lenientStubMapper() {
        lenient().when(mapper.toResponseDTO(any(Order.class)))
                .thenReturn(OrderResponseDTO.builder().build());
    }

    @Test
    void create_computesTotalPriceAndClearsCart_whenRequestIsValid() {
        when(authService.getCurrentUser()).thenReturn(client);
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(address));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderRequestDTO request = OrderRequestDTO.builder().deliveryAddressId(5L).build();

        orderService.create(request);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());

        Order saved = captor.getValue();
        assertThat(saved.getTotalPrice()).isEqualByComparingTo("25.98");
        assertThat(saved.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(saved.getOrderItems()).hasSize(1);
        assertThat(cart.getCartItems()).isEmpty();
    }

    @Test
    void create_throwsCartEmpty_whenCartHasNoItems() {
        cart.getCartItems().clear();
        when(authService.getCurrentUser()).thenReturn(client);
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));

        OrderRequestDTO request = OrderRequestDTO.builder().deliveryAddressId(5L).build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(CartEmptyException.class);

        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_throwsRestaurantClosed_whenRestaurantIsClosed() {
        restaurant.setStatus(RestaurantStatus.CLOSED);
        when(authService.getCurrentUser()).thenReturn(client);
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));

        OrderRequestDTO request = OrderRequestDTO.builder().deliveryAddressId(5L).build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(RestaurantClosedException.class);
    }

    @Test
    void create_throwsDishNotAvailable_whenDishIsNotAvailable() {
        dish.setStatus(DishStatus.OUT_OF_STOCK);
        when(authService.getCurrentUser()).thenReturn(client);
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));

        OrderRequestDTO request = OrderRequestDTO.builder().deliveryAddressId(5L).build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(DishNotAvailableException.class);
    }

    @Test
    void create_throwsUserAddressMismatch_whenAddressBelongsToAnotherUser() {
        User otherUser = User.builder().id(99L).build();
        address.setUser(otherUser);

        when(authService.getCurrentUser()).thenReturn(client);
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.of(cart));
        when(addressRepository.findById(5L)).thenReturn(Optional.of(address));

        OrderRequestDTO request = OrderRequestDTO.builder().deliveryAddressId(5L).build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(UserAddressMismatchException.class);
    }

    @Test
    void create_throwsEntityNotFound_whenCartIsMissing() {
        when(authService.getCurrentUser()).thenReturn(client);
        when(cartRepository.findByUserId(10L)).thenReturn(Optional.empty());

        OrderRequestDTO request = OrderRequestDTO.builder().deliveryAddressId(5L).build();

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void cancelOrder_setsStatusCancelled_whenOrderIsPending() {
        Order order = Order.builder().id(1L).user(client).restaurant(restaurant).status(OrderStatus.PENDING).build();
        when(authService.getCurrentUser()).thenReturn(client);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderService.cancelOrder(1L);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void cancelOrder_throwsInvalidOrderStatus_whenOrderIsNotPending() {
        Order order = Order.builder().id(1L).user(client).restaurant(restaurant).status(OrderStatus.CONFIRMED).build();
        when(authService.getCurrentUser()).thenReturn(client);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(InvalidOrderStatusException.class);
    }

    @Test
    void cancelOrder_throwsAccessDenied_whenUserIsNotOwnerClientOrAdmin() {
        Role otherRole = Role.builder().id(3L).name("CLIENT").build();
        User stranger = User.builder().id(999L).role(otherRole).build();
        Order order = Order.builder().id(1L).user(client).restaurant(restaurant).status(OrderStatus.PENDING).build();

        when(authService.getCurrentUser()).thenReturn(stranger);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateOrderStatus_throwsAccessDenied_whenCallerIsNeitherOwnerNorAdmin() {
        Role clientRole = Role.builder().id(1L).name("CLIENT").build();
        User stranger = User.builder().id(999L).role(clientRole).build();
        Order order = Order.builder().id(1L).user(client).restaurant(restaurant).status(OrderStatus.PENDING).build();

        when(authService.getCurrentUser()).thenReturn(stranger);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderStatusUpdateDTO request = new OrderStatusUpdateDTO();
        request.setStatus(OrderStatus.CONFIRMED);

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateOrderStatus_throwsInvalidOrderStatus_whenOrderAlreadyCompleted() {
        Order order = Order.builder().id(1L).user(client).restaurant(restaurant).status(OrderStatus.COMPLETED).build();
        when(authService.getCurrentUser()).thenReturn(restaurant.getUser());
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderStatusUpdateDTO request = new OrderStatusUpdateDTO();
        request.setStatus(OrderStatus.CANCELLED);

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, request))
                .isInstanceOf(InvalidOrderStatusException.class);
    }
}
