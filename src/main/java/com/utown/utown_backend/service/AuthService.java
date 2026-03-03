package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.RegisterRequestDTO;
import com.utown.utown_backend.dto.response.UserResponseDTO;
import com.utown.utown_backend.entity.Role;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.exception.EmailAlreadyExistsException;
import com.utown.utown_backend.repository.RoleRepository;
import com.utown.utown_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO register(RegisterRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        Role clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("CLIENT role not found"));

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(clientRole)
                .build();

        userRepository.save(user);

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .roleName(user.getRole().getName())
                .build();
    }
}
