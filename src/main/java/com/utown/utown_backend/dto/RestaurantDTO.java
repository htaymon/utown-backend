package com.utown.utown_backend.dto;

public class RestaurantDTO {
    private Long userId;
    private Long restaurantCategoryId;
    private String name;
    private String description;
    private String imageUrl;
    private Double minimumOrder;

    public RestaurantDTO() {
    }

    public RestaurantDTO(Long userId, Long restaurantCategoryId, String name, String description, String imageUrl, Double minimumOrder) {
        this.userId = userId;
        this.restaurantCategoryId = restaurantCategoryId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.minimumOrder = minimumOrder;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRestaurantCategoryId() {
        return restaurantCategoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Double getMinimumOrder() {
        return minimumOrder;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setRestaurantCategoryId(Long restaurantCategoryId) {
        this.restaurantCategoryId = restaurantCategoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setMinimumOrder(Double minimumOrder) {
        this.minimumOrder = minimumOrder;
    }
}
