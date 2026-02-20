package com.utown.utown_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_categories")
public class RestaurantCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long restaurantCategoryId;

    private String restaurantCategoryName;
    private String imageUrl;
    private Integer priority;

    public RestaurantCategory() {}

    public RestaurantCategory(String restaurantCategoryName, String imageUrl, Integer priority) {
        this.restaurantCategoryName = restaurantCategoryName;
        this.imageUrl = imageUrl;
        this.priority = priority;
    }

    public Long getRestaurantCategoryId() {
        return restaurantCategoryId;
    }

    public void setRestaurantCategoryId(Long restaurantCategoryId) {
        this.restaurantCategoryId = restaurantCategoryId;
    }

    public String getRestaurantCategoryName() {
        return restaurantCategoryName;
    }

    public void setRestaurantCategoryName(String restaurantCategoryName) {
        this.restaurantCategoryName = restaurantCategoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
