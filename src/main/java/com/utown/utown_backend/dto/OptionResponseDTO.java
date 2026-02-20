package com.utown.utown_backend.dto;

public class OptionResponseDTO {

    private Long optionId;
    private String name;
    private Double extraPrice;
    private Long dishId;
    private String dishName;

    public OptionResponseDTO() {}

    public OptionResponseDTO(Long optionId, String name, Double extraPrice, Long dishId, String dishName) {
        this.optionId = optionId;
        this.name = name;
        this.extraPrice = extraPrice;
        this.dishId = dishId;
        this.dishName = dishName;
    }

    public Long getOptionId() { return optionId; }
    public void setOptionId(Long optionId) { this.optionId = optionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getExtraPrice() { return extraPrice; }
    public void setExtraPrice(Double extraPrice) { this.extraPrice = extraPrice; }

    public Long getDishId() { return dishId; }
    public void setDishId(Long dishId) { this.dishId = dishId; }

    public String getDishName() { return dishName; }
    public void setDishName(String dishName) { this.dishName = dishName; }
}
