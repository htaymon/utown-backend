package com.utown.utown_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.repository.RoleRepository;
import com.utown.utown_backend.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Base for MockMvc integration tests running against the real Spring context
 * with the "test" profile (in-memory H2, no Flyway/Docker required).
 * Each test method runs inside a transaction that is rolled back afterwards,
 * so fixtures created via repositories or HTTP calls never leak between tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class AbstractIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected EntityManager entityManager;

    protected static final String RAW_PASSWORD = "Password123!";

    @BeforeEach
    void seedRoles() {
        for (String name : List.of("CLIENT", "RESTAURANT_ADMIN", "ADMIN")) {
            if (roleRepository.findByName(name).isEmpty()) {
                roleRepository.save(Role.builder().name(name).build());
            }
        }
    }

    /** Inserts a user directly with the given role, bypassing the public (CLIENT-only) registration endpoint. */
    protected User createUser(String email, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Role not seeded: " + roleName));

        User user = User.builder()
                .name(email)
                .email(email)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .phoneNumber("0900000000")
                .role(role)
                .build();

        return userRepository.save(user);
    }

    protected String loginAndGetToken(String email) throws Exception {
        String body = """
                {"email":"%s","password":"%s"}
                """.formatted(email, RAW_PASSWORD);

        String response = mockMvc.perform(
                        org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/auth/login")
                                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                .content(body))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    /**
     * The whole test method runs in one transaction/persistence context, unlike real
     * requests which each get their own. Clearing the first-level cache between
     * simulated "requests" avoids stale parent-side collections (e.g. a Cart's
     * cartItems loaded before a sibling CartItem row was inserted) that would never
     * actually occur across two independent HTTP requests in production.
     */
    protected void simulateNewRequest() {
        entityManager.flush();
        entityManager.clear();
    }
}
