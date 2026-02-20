package com.utown.utown_backend.dto;

public class RestaurantCategoryDTO {
    private String restaurantCategoryName;
    private String imageUrl;
    private Integer priority;

    public RestaurantCategoryDTO() {
    }

    public RestaurantCategoryDTO(String restaurantCategoryName, String imageUrl, Integer priority) {
        this.restaurantCategoryName = restaurantCategoryName;
        this.imageUrl = imageUrl;
        this.priority = priority;
    }

    public String getRestaurantCategoryName() {
        return restaurantCategoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setRestaurantCategoryName(String restaurantCategoryName) {
        this.restaurantCategoryName = restaurantCategoryName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
