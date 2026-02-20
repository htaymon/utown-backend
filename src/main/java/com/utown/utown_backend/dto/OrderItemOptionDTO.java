package com.utown.utown_backend.dto;

public class OrderItemOptionDTO {
    private Long orderItemOptionId;
    private Long orderItemId;
    private Long optionId;

    public OrderItemOptionDTO() {
    }

    public OrderItemOptionDTO(Long orderItemOptionId, Long orderItemId, Long optionId) {
        this.orderItemOptionId = orderItemOptionId;
        this.orderItemId = orderItemId;
        this.optionId = optionId;
    }

    public Long getOrderItemOptionId() {
        return orderItemOptionId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOrderItemOptionId(Long orderItemOptionId) {
        this.orderItemOptionId = orderItemOptionId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }
}
