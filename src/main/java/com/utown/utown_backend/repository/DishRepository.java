package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.Dish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DishRepository extends JpaRepository<Dish, Long> {

    @EntityGraph(attributePaths = {"restaurant", "dishCategory"})
    Page<Dish> findAll(Pageable pageable);
}
