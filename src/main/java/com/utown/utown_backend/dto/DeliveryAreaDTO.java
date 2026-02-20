package com.utown.utown_backend.dto;

public class DeliveryAreaDTO {
    private Long restaurantId;
    private String city;
    private String areaName;

    public DeliveryAreaDTO() {
    }

    public DeliveryAreaDTO(Long restaurantId, String city, String areaName) {
        this.restaurantId = restaurantId;
        this.city = city;
        this.areaName = areaName;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getCity() {
        return city;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
