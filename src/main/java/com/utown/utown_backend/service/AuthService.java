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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserResponseDTO register(RegisterRequestDTO request) {

        log.info("REGISTER request: email={}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("REGISTER failed: email already exists: email={}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already in use");
        }

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new EntityNotFoundException("CLIENT role not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(clientRole)
                .build();

        User saved = userRepository.save(user);

        log.info("REGISTER success: userId={}, email={}",
                saved.getId(), saved.getEmail());
        return UserResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .email(saved.getEmail())
                .phoneNumber(saved.getPhoneNumber())
                .roleName(saved.getRole().getName())
                .build();
    }


    public String login(LoginRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );
    }

    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("GET_CURRENT_USER failed: unauthenticated access");
            throw new AccessDeniedException("User not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("GET_CURRENT_USER failed: user not found for email={}", email);
                    return new EntityNotFoundException("User not found");
                });
    }
}