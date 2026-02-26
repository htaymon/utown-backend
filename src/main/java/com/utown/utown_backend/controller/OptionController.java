package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.OptionRequestDTO;
import com.utown.utown_backend.dto.response.OptionResponseDTO;
import com.utown.utown_backend.service.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public OptionResponseDTO create(@RequestBody OptionRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public OptionResponseDTO update(
            @PathVariable Long id,
            @RequestBody OptionRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}