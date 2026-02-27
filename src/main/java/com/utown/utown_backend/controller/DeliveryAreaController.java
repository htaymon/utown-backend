package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.request.DeliveryAreaRequestDTO;
import com.utown.utown_backend.dto.response.DeliveryAreaResponseDTO;
import com.utown.utown_backend.service.DeliveryAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-areas")
@RequiredArgsConstructor
public class DeliveryAreaController {

    private final DeliveryAreaService service;

    @PostMapping
    public DeliveryAreaResponseDTO create(@RequestBody DeliveryAreaRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<DeliveryAreaResponseDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DeliveryAreaResponseDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public DeliveryAreaResponseDTO update(@PathVariable Long id,
                                          @RequestBody DeliveryAreaRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}