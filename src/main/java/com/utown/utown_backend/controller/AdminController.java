package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.UserRequestDTO;
import com.utown.utown_backend.dto.response.UserResponseDTO;
import com.utown.utown_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "ADMIN SUCCESS";
    }

    @PostMapping("/create-restaurant-admin")
    public ResponseEntity<UserResponseDTO> createRestaurantAdmin(
            @Valid @RequestBody UserRequestDTO request) {

        UserResponseDTO user = userService.create(request);
        return ResponseEntity
                .created(URI.create("/users/" + user.getId()))
                .body(user);
    }
}
