package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.request.RestaurantCategoryRequestDTO;
import com.utown.utown_backend.dto.response.RestaurantCategoryResponseDTO;
import com.utown.utown_backend.entity.RestaurantCategory;
import com.utown.utown_backend.mapper.RestaurantCategoryMapper;
import com.utown.utown_backend.repository.RestaurantCategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantCategoryService {

    private final RestaurantCategoryRepository repository;
    private final RestaurantCategoryMapper mapper;

    public RestaurantCategoryResponseDTO create(RestaurantCategoryRequestDTO dto) {
        RestaurantCategory entity = mapper.toEntity(dto);
        repository.save(entity);
        return mapper.toResponseDTO(entity);
    }

    public List<RestaurantCategoryResponseDTO> getAll() {
        List<RestaurantCategory> categories = repository.findAll();
        return mapper.toResponseList(categories);
    }

    public RestaurantCategoryResponseDTO getById(Long id) {
        RestaurantCategory entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RestaurantCategory not found"));
        return mapper.toResponseDTO(entity);
    }

    public RestaurantCategoryResponseDTO update(Long id, RestaurantCategoryRequestDTO dto) {

        RestaurantCategory entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RestaurantCategory not found"));

        entity.setName(dto.getName());
        entity.setImageUrl(dto.getImageUrl());
        entity.setPriority(dto.getPriority());

        repository.save(entity);

        return mapper.toResponseDTO(entity);
    }

    public void delete(Long id) {
        RestaurantCategory entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("RestaurantCategory not found"));
        repository.delete(entity);
    }
}