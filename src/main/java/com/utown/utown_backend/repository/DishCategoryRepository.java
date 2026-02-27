package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.DishCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishCategoryRepository extends JpaRepository<DishCategory, Long> {
}
