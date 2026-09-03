package com.utown.utown_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utown.utown_backend.repository.CartItemRepository;
import com.utown.utown_backend.repository.CartRepository;
import com.utown.utown_backend.repository.OrderRepository;
import com.utown.utown_backend.repository.RoleRepository;
import com.utown.utown_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Real multithreaded regression tests for the two race conditions found in
 * {@code OrderService.create()} (double-submission from the same cart) and
 * {@code CartItemService.create()} (duplicate row for the same cart+dish).
 *
 * Deliberately NOT an {@link AbstractIntegrationTest} subclass: that base class wraps
 * each test method in one transaction that's rolled back at the end, which means a
 * second thread (its own connection/transaction) could never see fixture data the
 * first thread "created" — the whole point here is to exercise real concurrent
 * connections against a real committed database state, matching production.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ConcurrencyIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    void seedRoles() {
        TestSupport.seedRoles(roleRepository);
    }

    private long extractId(String responseBody) throws Exception {
        return objectMapper.readTree(responseBody).get("id").asLong();
    }

    private String createUserAndLogin(String email, String role) throws Exception {
        TestSupport.createUser(userRepository, roleRepository, passwordEncoder, email, role);
        return TestSupport.loginAndGetToken(mockMvc, objectMapper, email);
    }

    /** Sets up one restaurant with one dish, owned by a fresh RESTAURANT_ADMIN. Returns the dish ID. */
    private long setUpRestaurantWithDish(String ownerEmail, String suffix) throws Exception {
        String ownerToken = createUserAndLogin(ownerEmail, "RESTAURANT_ADMIN");

        String categoryBody = """
                {"name":"Category %s","imageUrl":"https://example.com/c.jpg","priority":1}
                """.formatted(suffix);
        long categoryId = extractId(mockMvc.perform(post("/restaurant-categories")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(categoryBody))
                .andReturn().getResponse().getContentAsString());

        String dishCategoryBody = """
                {"name":"DishCategory %s","imageUrl":"https://example.com/dc.jpg","priority":1}
                """.formatted(suffix);
        long dishCategoryId = extractId(mockMvc.perform(post("/dish-categories")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(dishCategoryBody))
                .andReturn().getResponse().getContentAsString());

        String restaurantBody = """
                {"restaurantCategoryId":%d,"name":"Restaurant %s","description":"desc","imageUrl":"https://example.com/r.jpg","minimumOrder":5.0,"status":"OPEN"}
                """.formatted(categoryId, suffix);
        long restaurantId = extractId(mockMvc.perform(post("/restaurants")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(restaurantBody))
                .andReturn().getResponse().getContentAsString());

        String dishBody = """
                {"restaurantId":%d,"dishCategoryId":%d,"name":"Dish %s","description":"desc","price":10.00,"image":"https://example.com/d.jpg","status":"AVAILABLE","priority":1}
                """.formatted(restaurantId, dishCategoryId, suffix);
        return extractId(mockMvc.perform(post("/dishes")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(dishBody))
                .andReturn().getResponse().getContentAsString());
    }

    /** Runs the given actions on separate threads, all starting as close to simultaneously as possible. */
    private List<Integer> runConcurrently(List<Callable<Integer>> actions) throws Exception {
        int n = actions.size();
        ExecutorService pool = Executors.newFixedThreadPool(n);
        CountDownLatch ready = new CountDownLatch(n);
        CountDownLatch start = new CountDownLatch(1);

        List<Future<Integer>> futures = new ArrayList<>();
        for (Callable<Integer> action : actions) {
            futures.add(pool.submit(() -> {
                ready.countDown();
                start.await();
                return action.call();
            }));
        }

        ready.await(5, TimeUnit.SECONDS);
        start.countDown();

        List<Integer> results = new ArrayList<>();
        for (Future<Integer> f : futures) {
            results.add(f.get(15, TimeUnit.SECONDS));
        }
        pool.shutdown();
        return results;
    }

    @Test
    void placingOrderTwiceConcurrentlyFromTheSameCart_createsOnlyOneOrder() throws Exception {
        long dishId = setUpRestaurantWithDish("race-owner-1@utown.dev", "A");

        String clientToken = createUserAndLogin("race-client-1@utown.dev", "CLIENT");

        String addressBody = """
                {"street":"1 Test St","city":"Seoul","state":"Seoul","postalCode":"00000"}
                """;
        long addressId = extractId(mockMvc.perform(post("/addresses")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(addressBody))
                .andReturn().getResponse().getContentAsString());

        String dishResponse = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/dishes/" + dishId)
                        .header("Authorization", "Bearer " + clientToken))
                .andReturn().getResponse().getContentAsString();
        long restaurantId = objectMapper.readTree(dishResponse).get("restaurantId").asLong();

        long cartId = extractId(mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"restaurantId\":" + restaurantId + "}"))
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(post("/cart-items")
                .header("Authorization", "Bearer " + clientToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"cartId\":" + cartId + ",\"dishId\":" + dishId + ",\"quantity\":1}"));

        String orderBody = "{\"deliveryAddressId\":" + addressId + "}";

        Callable<Integer> placeOrder = () -> mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderBody))
                .andReturn().getResponse().getStatus();

        List<Integer> statuses = runConcurrently(List.of(placeOrder, placeOrder));

        // Exactly one request should succeed (201); the other must see the now-empty
        // cart and get the same clean "cart is empty" 400 a real second click would —
        // never a duplicate order, and never a 500.
        assertThat(statuses).containsExactlyInAnyOrder(201, 400);

        long userId = userRepository.findByEmail("race-client-1@utown.dev").orElseThrow().getId();
        long orderCountForUser = orderRepository.findAll().stream()
                .filter(o -> o.getUser().getId().equals(userId))
                .count();
        assertThat(orderCountForUser).isEqualTo(1);
    }

    @Test
    void addingTheSameDishToCartConcurrently_neverCreatesDuplicateRows() throws Exception {
        long dishId = setUpRestaurantWithDish("race-owner-2@utown.dev", "B");

        String clientToken = createUserAndLogin("race-client-2@utown.dev", "CLIENT");

        String dishResponse = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/dishes/" + dishId)
                        .header("Authorization", "Bearer " + clientToken))
                .andReturn().getResponse().getContentAsString();
        long restaurantId = objectMapper.readTree(dishResponse).get("restaurantId").asLong();

        long cartId = extractId(mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"restaurantId\":" + restaurantId + "}"))
                .andReturn().getResponse().getContentAsString());

        String cartItemBody = "{\"cartId\":" + cartId + ",\"dishId\":" + dishId + ",\"quantity\":1}";

        Callable<Integer> addToCart = () -> mockMvc.perform(post("/cart-items")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cartItemBody))
                .andReturn().getResponse().getStatus();

        List<Integer> statuses = runConcurrently(List.of(addToCart, addToCart));

        // Both may succeed (one creates, one merges) or the loser of the race may get a
        // clean 409 from the unique constraint — either way, no 500 and no duplicate row.
        assertThat(statuses).allMatch(status -> status == 201 || status == 409);

        long rowsForThisDish = cartItemRepository.findAll().stream()
                .filter(ci -> ci.getCart().getId().equals(cartId) && ci.getDish().getId().equals(dishId))
                .count();
        assertThat(rowsForThisDish).isEqualTo(1);
    }
}
