package com.utown.utown_backend;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end walk through the core food-ordering flow: an ADMIN/RESTAURANT_ADMIN
 * sets up a restaurant and menu, a CLIENT builds a cart and places an order, and
 * role/ownership boundaries between users are verified along the way.
 */
class OrderFlowIntegrationTest extends AbstractIntegrationTest {

    private String ownerToken;
    private String clientToken;

    private void setUpMenuAndActors() throws Exception {
        createUser("owner@utown.dev", "RESTAURANT_ADMIN");
        createUser("client@utown.dev", "CLIENT");
        ownerToken = loginAndGetToken("owner@utown.dev");
        clientToken = loginAndGetToken("client@utown.dev");
    }

    private long extractId(String responseBody) throws Exception {
        return objectMapper.readTree(responseBody).get("id").asLong();
    }

    @Test
    void clientCanBrowseMenuBuildCartAndPlaceOrder() throws Exception {
        setUpMenuAndActors();

        String categoryBody = """
                {"name":"Korean Food","imageUrl":"https://example.com/korean.jpg","priority":1}
                """;
        long categoryId = extractId(mockMvc.perform(post("/restaurant-categories")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(categoryBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        String dishCategoryBody = """
                {"name":"Main Course","imageUrl":"https://example.com/main.jpg","priority":1}
                """;
        long dishCategoryId = extractId(mockMvc.perform(post("/dish-categories")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(dishCategoryBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        String restaurantBody = """
                {"restaurantCategoryId":%d,"name":"Seoul BBQ","description":"Korean BBQ","imageUrl":"https://example.com/r.jpg","minimumOrder":5.0,"status":"OPEN"}
                """.formatted(categoryId);
        long restaurantId = extractId(mockMvc.perform(post("/restaurants")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(restaurantBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        String dishBody = """
                {"restaurantId":%d,"dishCategoryId":%d,"name":"Bulgogi Bowl","description":"Beef bulgogi over rice","price":10.00,"image":"https://example.com/d.jpg","status":"AVAILABLE","priority":1}
                """.formatted(restaurantId, dishCategoryId);
        long dishId = extractId(mockMvc.perform(post("/dishes")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON).content(dishBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        String addressBody = """
                {"street":"123 Gangnam-daero","city":"Seoul","state":"Seoul","postalCode":"06018"}
                """;
        long addressId = extractId(mockMvc.perform(post("/addresses")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(addressBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        String cartBody = """
                {"restaurantId":%d}
                """.formatted(restaurantId);
        long cartId = extractId(mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(cartBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());

        String cartItemBody = """
                {"cartId":%d,"dishId":%d,"quantity":2}
                """.formatted(cartId, dishId);
        mockMvc.perform(post("/cart-items")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(cartItemBody))
                .andExpect(status().isCreated());

        // A real order-placement request would load its own fresh Cart from the
        // database; simulate that instead of reading the stale in-memory Cart
        // this test method already fetched earlier in the same transaction.
        simulateNewRequest();

        String orderBody = """
                {"deliveryAddressId":%d}
                """.formatted(addressId);
        String orderResponse = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(orderBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(20.00))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString();
        long orderId = extractId(orderResponse);
        simulateNewRequest();

        mockMvc.perform(get("/orders/my").header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(get("/carts/my").header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartItems.length()").value(0));

        // CLIENT cannot manage restaurants (role boundary).
        mockMvc.perform(post("/restaurants")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(restaurantBody))
                .andExpect(status().isForbidden());

        // A different client cannot cancel someone else's order (ownership boundary).
        createUser("intruder@utown.dev", "CLIENT");
        String intruderToken = loginAndGetToken("intruder@utown.dev");
        simulateNewRequest();
        mockMvc.perform(put("/orders/" + orderId + "/cancel")
                        .header("Authorization", "Bearer " + intruderToken))
                .andExpect(status().isForbidden());

        // The restaurant owner can move the order forward.
        simulateNewRequest();
        mockMvc.perform(patch("/orders/" + orderId + "/status")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void notification_cannotBeReadByAnotherUser() throws Exception {
        setUpMenuAndActors();

        String notificationBody = """
                {"message":"Your order is on its way","status":"UNREAD"}
                """;
        String response = mockMvc.perform(post("/notifications")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(notificationBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long notificationId = extractId(response);

        mockMvc.perform(get("/notifications/" + notificationId)
                        .header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk());

        createUser("intruder@utown.dev", "CLIENT");
        String intruderToken = loginAndGetToken("intruder@utown.dev");

        mockMvc.perform(get("/notifications/" + notificationId)
                        .header("Authorization", "Bearer " + intruderToken))
                .andExpect(status().isForbidden());
    }
}
