package com.utown.utown_backend;

import com.utown.utown_backend.entity.User;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationFlowIntegrationTest extends AbstractIntegrationTest {

    @Test
    void register_returns201_withCreatedUser() throws Exception {
        String body = """
                {"name":"Jane Student","email":"jane@utown.dev","password":"Password123!","phoneNumber":"0900000000"}
                """;

        mockMvc.perform(post("/auth/register").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("jane@utown.dev"))
                .andExpect(jsonPath("$.roleName").value("CLIENT"));
    }

    @Test
    void register_returns409_whenEmailAlreadyExists() throws Exception {
        createUser("jane@utown.dev", "CLIENT");

        String body = """
                {"name":"Jane Student","email":"jane@utown.dev","password":"Password123!","phoneNumber":"0900000000"}
                """;

        mockMvc.perform(post("/auth/register").contentType("application/json").content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    void register_returns400_whenFieldsAreMissing() throws Exception {
        String body = """
                {"name":"","email":"not-an-email","password":"123","phoneNumber":""}
                """;

        mockMvc.perform(post("/auth/register").contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }

    @Test
    void login_returnsJwtToken_whenCredentialsAreValid() throws Exception {
        createUser("jane@utown.dev", "CLIENT");

        String body = """
                {"email":"jane@utown.dev","password":"%s"}
                """.formatted(RAW_PASSWORD);

        mockMvc.perform(post("/auth/login").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_returns401_whenPasswordIsWrong() throws Exception {
        createUser("jane@utown.dev", "CLIENT");

        String body = """
                {"email":"jane@utown.dev","password":"wrong-password"}
                """;

        mockMvc.perform(post("/auth/login").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    void protectedEndpoint_returns401_withoutToken() throws Exception {
        mockMvc.perform(get("/orders/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicRestaurantListing_returns200_withoutToken() throws Exception {
        mockMvc.perform(get("/restaurants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void protectedEndpoint_returns401_whenTokenReferencesADeletedUser() throws Exception {
        User ghost = createUser("ghost@utown.dev", "CLIENT");
        String token = loginAndGetToken("ghost@utown.dev");

        userRepository.delete(ghost);
        simulateNewRequest();

        mockMvc.perform(get("/orders/my").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHENTICATED"));
    }
}
