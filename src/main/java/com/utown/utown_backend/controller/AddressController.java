package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.AddressRequestDTO;
import com.utown.utown_backend.dto.response.AddressResponseDTO;
import com.utown.utown_backend.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService service;

    @GetMapping
    public List<AddressResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AddressResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public AddressResponseDTO create(
            @RequestBody AddressRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public AddressResponseDTO update(
            @PathVariable Long id,
            @RequestBody AddressRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}