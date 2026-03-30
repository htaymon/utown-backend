package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.OrderItemOptionRequestDTO;
import com.utown.utown_backend.dto.response.OrderItemOptionResponseDTO;
import com.utown.utown_backend.service.OrderItemOptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/order-item-options")
@RequiredArgsConstructor
public class OrderItemOptionController {

    private final OrderItemOptionService service;

    @PostMapping
    public ResponseEntity<OrderItemOptionResponseDTO> create(
            @Valid @RequestBody OrderItemOptionRequestDTO dto) {
        OrderItemOptionResponseDTO response = service.create(dto);
        URI location = URI.create("/order-item-options/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderItemOptionResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderItemOptionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderItemOptionResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemOptionRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
