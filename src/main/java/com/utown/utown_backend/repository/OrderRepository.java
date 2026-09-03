package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Only to-one associations are eagerly fetched here. Fetch-joining the
    // orderItems collection together with a Pageable forces Hibernate to page
    // in memory (it can't LIMIT/OFFSET a join fetch without risking duplicate/
    // truncated rows), which defeats pagination entirely on a large table.
    // orderItems is left lazy and loads per order, bounded by the page size.
    @EntityGraph(attributePaths = {"user", "restaurant", "deliveryAddress"})
    Page<Order> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "restaurant", "deliveryAddress"})
    Page<Order> findByRestaurantId(Long restaurantId, Pageable pageable);

}
