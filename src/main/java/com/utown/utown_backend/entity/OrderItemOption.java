package com.utown.utown_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_item_options")
public class OrderItemOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemOptionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "option_id")
    private Option option;

    public OrderItemOption() {}

    public OrderItemOption(OrderItem orderItem, Option option) {
        this.orderItem = orderItem;
        this.option = option;
    }

    public Long getOrderItemOptionId() {
        return orderItemOptionId;
    }

    public void setOrderItemOptionId(Long orderItemOptionId) {
        this.orderItemOptionId = orderItemOptionId;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }
}
