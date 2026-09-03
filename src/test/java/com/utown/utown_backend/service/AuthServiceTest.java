package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.LoginRequestDTO;
import com.utown.utown_backend.dto.request.RegisterRequestDTO;
import com.utown.utown_backend.dto.response.UserResponseDTO;
import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.exception.EmailAlreadyExistsException;
import com.utown.utown_backend.exception.InvalidCredentialsException;
import com.utown.utown_backend.repository.RoleRepository;
import com.utown.utown_backend.repository.UserRepository;
import com.utown.utown_backend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private Role clientRole;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequestDTO.builder()
                .name("Jane Student")
                .email("jane@utown.dev")
                .password("password123")
                .phoneNumber("0900000000")
                .build();

        clientRole = Role.builder().id(1L).name("CLIENT").build();
    }

    @Test
    void register_createsUserWithClientRole_whenEmailIsNew() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(roleRepository.findByName("CLIENT")).thenReturn(Optional.of(clientRole));
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(42L);
            return u;
        });

        UserResponseDTO response = authService.register(registerRequest);

        assertThat(response.getId()).isEqualTo(42L);
        assertThat(response.getEmail()).isEqualTo("jane@utown.dev");
        assertThat(response.getRoleName()).isEqualTo("CLIENT");

        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throwsEmailAlreadyExists_whenEmailIsTaken() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_returnsToken_whenCredentialsAreValid() {
        User user = User.builder()
                .id(1L)
                .email("jane@utown.dev")
                .password("hashed-password")
                .role(clientRole)
                .build();

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("jane@utown.dev");
        request.setPassword("password123");

        when(userRepository.findByEmail("jane@utown.dev")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtUtil.generateToken("jane@utown.dev", "CLIENT")).thenReturn("jwt-token");

        String token = authService.login(request);

        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    void login_throwsInvalidCredentials_whenEmailNotFound() {
        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("missing@utown.dev");
        request.setPassword("password123");

        when(userRepository.findByEmail("missing@utown.dev")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void login_throwsInvalidCredentials_whenPasswordDoesNotMatch() {
        User user = User.builder()
                .id(1L)
                .email("jane@utown.dev")
                .password("hashed-password")
                .role(clientRole)
                .build();

        LoginRequestDTO request = new LoginRequestDTO();
        request.setEmail("jane@utown.dev");
        request.setPassword("wrong-password");

        when(userRepository.findByEmail("jane@utown.dev")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }
}
