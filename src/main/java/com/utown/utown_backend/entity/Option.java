package com.utown.utown_backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "options")
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long optionId;

    @ManyToOne
    @JoinColumn(name = "dish_id")
    private Dish dish;

    private String name;
    private Double extraPrice;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL)
    private List<OrderItemOption> orderItemOptions;

    public Option() {}

    public Option(Dish dish, String name, Double extraPrice) {
        this.dish = dish;
        this.name = name;
        this.extraPrice = extraPrice;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(Double extraPrice) {
        this.extraPrice = extraPrice;
    }

    public List<OrderItemOption> getOrderItemOptions() {
        return orderItemOptions;
    }

    public void setOrderItemOptions(List<OrderItemOption> orderItemOptions) {
        this.orderItemOptions = orderItemOptions;
    }
}
