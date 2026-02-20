package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.DeliveryAreaDTO;
import com.utown.utown_backend.entity.DeliveryArea;
import com.utown.utown_backend.service.DeliveryAreaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery-areas")
public class DeliveryAreaController {

    private final DeliveryAreaService service;

    public DeliveryAreaController(DeliveryAreaService service) {
        this.service = service;
    }

    @GetMapping
    public List<DeliveryArea> getAllAreas() {
        return service.getAllAreas();
    }

    @GetMapping("/{id}")
    public DeliveryArea getAreaById(@PathVariable Long id) {
        return service.getAreaById(id);
    }

    @PostMapping
    public DeliveryArea createArea(@RequestBody DeliveryAreaDTO dto) {
        return service.createArea(dto);
    }

    @PutMapping("/{id}")
    public DeliveryArea updateArea(@PathVariable Long id, @RequestBody DeliveryAreaDTO dto) {
        return service.updateArea(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteArea(@PathVariable Long id) {
        service.deleteArea(id);
    }
}
