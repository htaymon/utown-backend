package com.utown.utown_backend.dto;

public class DishCategoryDTO {
    private String dishCategoryName;
    private String imageUrl;
    private Integer priority;

    public DishCategoryDTO() {
    }

    public DishCategoryDTO(String dishCategoryName, String imageUrl, Integer priority) {
        this.dishCategoryName = dishCategoryName;
        this.imageUrl = imageUrl;
        this.priority = priority;
    }

    public String getDishCategoryName() {
        return dishCategoryName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setDishCategoryName(String dishCategoryName) {
        this.dishCategoryName = dishCategoryName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
