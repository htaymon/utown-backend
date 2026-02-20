package com.utown.utown_backend.service;

import com.utown.utown_backend.dto.DishCategoryDTO;
import com.utown.utown_backend.entity.DishCategory;
import com.utown.utown_backend.repository.DishCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DishCategoryService {

    private final DishCategoryRepository repository;

    public DishCategoryService(DishCategoryRepository repository) {
        this.repository = repository;
    }

    public List<DishCategory> getAllCategories() {
        return repository.findAll();
    }

    public DishCategory getCategoryById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public DishCategory createCategory(DishCategoryDTO dto) {
        DishCategory category = new DishCategory(dto.getDishCategoryName(), dto.getImageUrl(), dto.getPriority());
        return repository.save(category);
    }

    public DishCategory updateCategory(Long id, DishCategoryDTO dto) {
        DishCategory category = repository.findById(id).orElse(null);
        if (category != null) {
            category.setDishCategoryName(dto.getDishCategoryName());
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
