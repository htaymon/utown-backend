package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.RestaurantCategoryDTO;
import com.utown.utown_backend.entity.RestaurantCategory;
import com.utown.utown_backend.repository.RestaurantCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantCategoryService {

    private final RestaurantCategoryRepository repository;

    public RestaurantCategoryService(RestaurantCategoryRepository repository) {
        this.repository = repository;
    }

    public List<RestaurantCategory> getAllCategories() {
        return repository.findAll();
    }

    public RestaurantCategory getCategoryById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public RestaurantCategory createCategory(RestaurantCategoryDTO dto) {
        RestaurantCategory category = new RestaurantCategory(
                dto.getRestaurantCategoryName(),
                dto.getImageUrl(), dto.getPriority()
        );
        return repository.save(category);
    }

    public RestaurantCategory updateCategory(Long id, RestaurantCategoryDTO dto) {
        RestaurantCategory category = repository.findById(id).orElse(null);
        if (category != null) {
            category.setRestaurantCategoryName(dto.getRestaurantCategoryName());
            category.setImageUrl(dto.getImageUrl());
            category.setPriority(dto.getPriority());
            return repository.save(category);
        }
        return null;
    }

    public void deleteCategory(Long id) {
        repository.deleteById(id);
    }
}
