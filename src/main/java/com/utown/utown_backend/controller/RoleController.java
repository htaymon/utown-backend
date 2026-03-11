package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.RoleRequestDTO;
import com.utown.utown_backend.dto.response.RoleResponseDTO;
import com.utown.utown_backend.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService service;

    @PostMapping
    public ResponseEntity<RoleResponseDTO> create(@Valid @RequestBody RoleRequestDTO dto) {

        RoleResponseDTO response = service.create(dto);
        URI location = URI.create("/roles/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RoleResponseDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}