package com.utown.utown_backend;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Non-concurrent behavior of CartItemService.create(): new item vs. quantity merge. */
class CartItemFlowIntegrationTest extends AbstractIntegrationTest {

    private long extractId(String responseBody) throws Exception {
        return objectMapper.readTree(responseBody).get("id").asLong();
    }

    private long[] setUpCartWithDish() throws Exception {
        createUser("cartowner@utown.dev", "RESTAURANT_ADMIN");
        String ownerToken = loginAndGetToken("cartowner@utown.dev");

        long categoryId = extractId(mockMvc.perform(post("/restaurant-categories")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Cat\",\"imageUrl\":\"https://example.com/c.jpg\",\"priority\":1}"))
                .andReturn().getResponse().getContentAsString());

        long dishCategoryId = extractId(mockMvc.perform(post("/dish-categories")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"DCat\",\"imageUrl\":\"https://example.com/dc.jpg\",\"priority\":1}"))
                .andReturn().getResponse().getContentAsString());

        long restaurantId = extractId(mockMvc.perform(post("/restaurants")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"restaurantCategoryId\":" + categoryId
                                + ",\"name\":\"R\",\"description\":\"d\",\"imageUrl\":\"https://example.com/r.jpg\",\"minimumOrder\":5.0,\"status\":\"OPEN\"}"))
                .andReturn().getResponse().getContentAsString());

        long dishId = extractId(mockMvc.perform(post("/dishes")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"restaurantId\":" + restaurantId + ",\"dishCategoryId\":" + dishCategoryId
                                + ",\"name\":\"Dish\",\"description\":\"d\",\"price\":10.00,\"image\":\"https://example.com/d.jpg\",\"status\":\"AVAILABLE\",\"priority\":1}"))
                .andReturn().getResponse().getContentAsString());

        createUser("cartclient@utown.dev", "CLIENT");
        String clientToken = loginAndGetToken("cartclient@utown.dev");

        long cartId = extractId(mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"restaurantId\":" + restaurantId + "}"))
                .andReturn().getResponse().getContentAsString());

        return new long[]{cartId, dishId};
    }

    @Test
    void create_addsNewRow_whenDishNotAlreadyInCart() throws Exception {
        long[] ids = setUpCartWithDish();
        long cartId = ids[0];
        long dishId = ids[1];
        String clientToken = loginAndGetToken("cartclient@utown.dev");

        mockMvc.perform(post("/cart-items")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cartId\":" + cartId + ",\"dishId\":" + dishId + ",\"quantity\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(2));

        // See AbstractIntegrationTest#simulateNewRequest: within this one test transaction,
        // Cart.cartItems may already be cached (empty) from an earlier read in this same
        // persistence context. A real second HTTP request wouldn't have that problem.
        simulateNewRequest();

        mockMvc.perform(get("/cart-items").header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void create_mergesQuantity_whenDishAlreadyInCart() throws Exception {
        long[] ids = setUpCartWithDish();
        long cartId = ids[0];
        long dishId = ids[1];
        String clientToken = loginAndGetToken("cartclient@utown.dev");

        String body = "{\"cartId\":" + cartId + ",\"dishId\":" + dishId + ",\"quantity\":2}";

        mockMvc.perform(post("/cart-items")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(2));

        // Same dish added again: should merge into the existing row's quantity (2 + 2 = 4),
        // not create a second row.
        mockMvc.perform(post("/cart-items")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity").value(4));

        simulateNewRequest();

        mockMvc.perform(get("/cart-items").header("Authorization", "Bearer " + clientToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].quantity").value(4));
    }
}
