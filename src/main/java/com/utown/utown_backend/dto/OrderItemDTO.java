package com.utown.utown_backend.dto;

import java.util.List;

public class OrderItemDTO {
    private Long orderItemId;
    private Long orderId;
    private Long dishId;
    private Integer quantity;
    private Double price;
    private List<OrderItemOptionDTO> orderItemOptions;

    public OrderItemDTO() {
    }

    public OrderItemDTO(Long orderItemId, Long orderId, Long dishId, Integer quantity, Double price,
                        List<OrderItemOptionDTO> orderItemOptions) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.dishId = dishId;
        this.quantity = quantity;
        this.price = price;
        this.orderItemOptions = orderItemOptions;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getDishId() {
        return dishId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public List<OrderItemOptionDTO> getOrderItemOptions() {
        return orderItemOptions;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setOrderItemOptions(List<OrderItemOptionDTO> orderItemOptions) {
        this.orderItemOptions = orderItemOptions;
    }
}
