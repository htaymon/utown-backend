package com.utown.utown_backend.controller;

import com.utown.utown_backend.dto.OptionDTO;
import com.utown.utown_backend.dto.OptionResponseDTO;
import com.utown.utown_backend.entity.Option;
import com.utown.utown_backend.service.OptionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/options")
public class OptionController {

    private final OptionService service;

    public OptionController(OptionService service) {
        this.service = service;
    }

    @GetMapping
    public List<OptionResponseDTO> getAll() {
        return service.getAllOptions();
    }

    @GetMapping("/{id}")
    public OptionResponseDTO getById(@PathVariable Long id) {
        return service.getOptionById(id);
    }
    @PostMapping
    public Option create(@RequestBody OptionDTO dto) {
        return service.createOption(dto);
    }

    @PutMapping("/{id}")
    public Option update(@PathVariable Long id, @RequestBody OptionDTO dto) {
        return service.updateOption(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteOption(id);
    }
}
