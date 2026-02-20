package com.utown.utown_backend.dto;

public class OptionDTO {
    private Long dishId;
    private String name;
    private Double extraPrice;

    public OptionDTO() {
    }

    public OptionDTO(Long dishId, String name, Double extraPrice) {
        this.dishId = dishId;
        this.name = name;
        this.extraPrice = extraPrice;
    }

    public Long getDishId() {
        return dishId;
    }

    public String getName() {
        return name;
    }

    public Double getExtraPrice() {
        return extraPrice;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExtraPrice(Double extraPrice) {
        this.extraPrice = extraPrice;
    }
}
