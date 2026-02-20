package com.utown.utown_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "dish_categories")
public class DishCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dishCategoryId;

    private String dishCategoryName;
    private String imageUrl;
    private Integer priority;

    public DishCategory() {}

    public DishCategory(String dishCategoryName, String imageUrl, Integer priority) {
        this.dishCategoryName = dishCategoryName;
        this.imageUrl = imageUrl;
        this.priority = priority;
    }

    public Long getDishCategoryId() {
        return dishCategoryId;
    }

    public void setDishCategoryId(Long dishCategoryId) {
        this.dishCategoryId = dishCategoryId;
    }

    public String getDishCategoryName() {
        return dishCategoryName;
    }

    public void setDishCategoryName(String dishCategoryName) {
        this.dishCategoryName = dishCategoryName;
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
