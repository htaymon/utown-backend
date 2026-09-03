package com.utown.utown_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.repository.RoleRepository;
import com.utown.utown_backend.repository.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Plain (non-JUnit-lifecycle) test fixture helpers shared between
 * {@link AbstractIntegrationTest} subclasses (which run each test in a single
 * rolled-back transaction) and tests that must NOT be wrapped in one transaction,
 * e.g. real multithreaded concurrency tests where a second thread needs its own
 * connection to see data the first thread already committed.
 */
final class TestSupport {

    static final String RAW_PASSWORD = "Password123!";

    private TestSupport() {
    }

    static void seedRoles(RoleRepository roleRepository) {
        for (String name : List.of("CLIENT", "RESTAURANT_ADMIN", "ADMIN")) {
            if (roleRepository.findByName(name).isEmpty()) {
                roleRepository.save(Role.builder().name(name).build());
            }
        }
    }

    /** Inserts a user directly with the given role, bypassing the public (CLIENT-only) registration endpoint. */
    static User createUser(UserRepository userRepository, RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder, String email, String roleName) {
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

    static String loginAndGetToken(MockMvc mockMvc, ObjectMapper objectMapper, String email) throws Exception {
        String body = """
                {"email":"%s","password":"%s"}
                """.formatted(email, RAW_PASSWORD);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }
}
