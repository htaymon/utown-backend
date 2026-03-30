package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

    public interface CartItemRepository extends JpaRepository<CartItem, Long> {
        Optional<CartItem> findByCartIdAndDishId(Long cartId, Long dishId);
    }
