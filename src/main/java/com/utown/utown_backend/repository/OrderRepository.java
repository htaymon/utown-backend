package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByRestaurantId(Long restaurantId, Pageable pageable);

}
