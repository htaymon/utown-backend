package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.OptionRequestDTO;
import com.utown.utown_backend.dto.response.OptionResponseDTO;
import com.utown.utown_backend.service.OptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/options")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService service;

    @GetMapping
    public List<OptionResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public OptionResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<OptionResponseDTO> create(@Valid @RequestBody OptionRequestDTO dto) {

        OptionResponseDTO response = service.create(dto);
        URI location = URI.create("/options/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{id}")
    public OptionResponseDTO update(
            @PathVariable Long id,
            @Valid @RequestBody OptionRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}