package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.DishCategoryRequestDTO;
import com.utown.utown_backend.dto.response.DishCategoryResponseDTO;
import com.utown.utown_backend.entity.DishCategory;
import com.utown.utown_backend.mapper.DishCategoryMapper;
import com.utown.utown_backend.repository.DishCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishCategoryService {

    private final DishCategoryRepository repository;
    private final DishCategoryMapper mapper;

    public DishCategoryResponseDTO create(DishCategoryRequestDTO dto) {
        DishCategory entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    public List<DishCategoryResponseDTO> getAll() {
        return mapper.toResponseList(repository.findAll());
    }

    public DishCategoryResponseDTO getById(Long id) {
        DishCategory entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DishCategory not found"));
        return mapper.toResponseDTO(entity);
    }

    public DishCategoryResponseDTO update(Long id, DishCategoryRequestDTO dto) {

        DishCategory entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DishCategory not found"));

        entity.setName(dto.getName());
        entity.setImageUrl(dto.getImageUrl());
        entity.setPriority(dto.getPriority());

        repository.save(entity);

        return mapper.toResponseDTO(entity);
    }

    public void delete(Long id) {
        DishCategory entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DishCategory not found"));
        repository.delete(entity);
    }
}