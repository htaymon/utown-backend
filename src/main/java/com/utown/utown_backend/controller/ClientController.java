package com.utown.utown_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
public class ClientController {

    @GetMapping("/profile")
    public String profile() {
        return "CLIENT SUCCESS";
    }
}
