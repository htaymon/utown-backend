package com.utown.utown_backend.dto;

public class DishDTO {
    private Long restaurantId;
    private Long dishCategoryId;
    private String name;
    private String description;
    private Double price;
    private String image;
    private String status;
    private Integer priority;

    public DishDTO() {
    }

    public DishDTO(Long restaurantId, Long dishCategoryId, String name, String description,
                   Double price, String image, String status, Integer priority) {
        this.restaurantId = restaurantId;
        this.dishCategoryId = dishCategoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.status = status;
        this.priority = priority;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public Long getDishCategoryId() {
        return dishCategoryId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setDishCategoryId(Long dishCategoryId) {
        this.dishCategoryId = dishCategoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
