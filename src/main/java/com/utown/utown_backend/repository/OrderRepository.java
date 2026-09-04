package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"user", "restaurant", "deliveryAddress"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "restaurant", "deliveryAddress"})
    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);

}
