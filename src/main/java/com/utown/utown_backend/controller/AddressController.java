package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.AddressRequestDTO;
import com.utown.utown_backend.dto.response.AddressResponseDTO;
import com.utown.utown_backend.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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
    public ResponseEntity<AddressResponseDTO> create(@Valid @RequestBody AddressRequestDTO dto) {
        AddressResponseDTO response = service.create(dto);
        URI location = URI.create("/addresses/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public AddressResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}