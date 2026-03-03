package com.utown.utown_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurant-admin")
@RequiredArgsConstructor
public class RestaurantAdminController {

    @GetMapping("/dashboard")
    public String dashboard() {
        return "RESTAURANT ADMIN SUCCESS";
    }
}
