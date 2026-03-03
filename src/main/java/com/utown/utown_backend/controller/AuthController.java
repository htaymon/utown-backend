package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.LoginRequestDTO;
import com.utown.utown_backend.dto.request.RegisterRequestDTO;
import com.utown.utown_backend.dto.response.UserResponseDTO;
import com.utown.utown_backend.entity.User;
import com.utown.utown_backend.repository.UserRepository;
import com.utown.utown_backend.security.JwtUtil;
import com.utown.utown_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthService authService;


    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(
            @RequestBody RegisterRequestDTO request) {

        return ResponseEntity.ok(authService.register(request));
    }
    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().getName()
        );
    }
}
