package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {
}
