package com.utown.utown_backend.repository;

import com.utown.utown_backend.entity.Cart;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUserId(Long userId);
    boolean existsByUserId(Long userId);

    /**
     * Locks the cart row for the duration of the caller's transaction (SELECT ... FOR UPDATE).
     * Used by order creation so that two concurrent "place order" requests for the same cart
     * are serialized instead of both reading the same items and creating two orders: the second
     * request blocks until the first transaction commits (and clears the cart), then correctly
     * sees an empty cart.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Cart c where c.user.id = :userId")
    Optional<Cart> findByUserIdForUpdate(@Param("userId") Long userId);
}
