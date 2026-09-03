package com.utown.utown_backend.security;

import com.utown.utown_backend.entity.Restaurant;
import com.utown.utown_backend.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RestaurantAccessGuard {

    public void check(Restaurant restaurant, User user) {
        boolean isOwner = restaurant.getUser().getId().equals(user.getId());
        boolean isAdmin = user.getRole() != null && "ADMIN".equals(user.getRole().getName());

        if (!isOwner && !isAdmin) {
            log.warn("RESTAURANT_ACCESS_DENIED: restaurantId={}, userId={}",
                    restaurant.getId(), user.getId());
            throw new AccessDeniedException("Access denied");
        }
    }
}
