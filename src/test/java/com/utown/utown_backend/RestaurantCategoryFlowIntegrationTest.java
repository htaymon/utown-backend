package com.utown.utown_backend;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Full CRUD for RestaurantCategory, mirroring the existing DishCategory conventions
 * (role-gated mutations, open reads, EntityNotFoundException -> 404).
 */
class RestaurantCategoryFlowIntegrationTest extends AbstractIntegrationTest {

    private long extractId(String responseBody) throws Exception {
        return objectMapper.readTree(responseBody).get("id").asLong();
    }

    @Test
    void fullCrudLifecycle_worksForAdminOrRestaurantAdmin() throws Exception {
        createUser("category-admin@utown.dev", "ADMIN");
        String adminToken = loginAndGetToken("category-admin@utown.dev");

        String createBody = """
                {"name":"Korean Food","imageUrl":"https://example.com/korean.jpg","priority":1}
                """;
        long id = extractId(mockMvc.perform(post("/restaurant-categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Korean Food"))
                .andReturn().getResponse().getContentAsString());

        mockMvc.perform(get("/restaurant-categories/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Korean Food"));

        mockMvc.perform(get("/restaurant-categories")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        String updateBody = """
                {"name":"Japanese Food","imageUrl":"https://example.com/japanese.jpg","priority":2}
                """;
        mockMvc.perform(put("/restaurant-categories/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(updateBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Japanese Food"))
                .andExpect(jsonPath("$.priority").value(2));

        mockMvc.perform(delete("/restaurant-categories/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/restaurant-categories/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void update_returns404_whenCategoryDoesNotExist() throws Exception {
        createUser("category-admin2@utown.dev", "ADMIN");
        String adminToken = loginAndGetToken("category-admin2@utown.dev");

        String updateBody = """
                {"name":"Doesn't matter","imageUrl":"https://example.com/x.jpg","priority":1}
                """;
        mockMvc.perform(put("/restaurant-categories/999999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(updateBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_returns403_forClientRole() throws Exception {
        createUser("category-client@utown.dev", "CLIENT");
        String clientToken = loginAndGetToken("category-client@utown.dev");

        String createBody = """
                {"name":"Korean Food","imageUrl":"https://example.com/korean.jpg","priority":1}
                """;
        mockMvc.perform(post("/restaurant-categories")
                        .header("Authorization", "Bearer " + clientToken)
                        .contentType(MediaType.APPLICATION_JSON).content(createBody))
                .andExpect(status().isForbidden());
    }
}
