package com.utown.utown_backend;

import com.fasterxml.jackson.databind.ObjectMapper;
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

/**
 * Base for MockMvc integration tests running against the real Spring context
 * with the "test" profile (in-memory H2, no Flyway/Docker required).
 * Each test method runs inside a transaction that is rolled back afterwards,
 * so fixtures created via repositories or HTTP calls never leak between tests.
 *
 * Not suitable for real multithreaded concurrency tests (a second thread's own
 * transaction/connection can't see data this test's still-open transaction hasn't
 * committed) — see {@link TestSupport} and {@link ConcurrencyIntegrationTest} for that.
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

    protected static final String RAW_PASSWORD = TestSupport.RAW_PASSWORD;

    @BeforeEach
    void seedRoles() {
        TestSupport.seedRoles(roleRepository);
    }

    /** Inserts a user directly with the given role, bypassing the public (CLIENT-only) registration endpoint. */
    protected User createUser(String email, String roleName) {
        return TestSupport.createUser(userRepository, roleRepository, passwordEncoder, email, roleName);
    }

    protected String loginAndGetToken(String email) throws Exception {
        return TestSupport.loginAndGetToken(mockMvc, objectMapper, email);
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
