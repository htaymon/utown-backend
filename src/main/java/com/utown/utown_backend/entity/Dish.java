package com.utown.utown_backend.entity;

import com.utown.utown_backend.enums.DishStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "dishes")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dish extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_category_id", nullable = false)
    private DishCategory dishCategory;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DishStatus status;

    private Integer priority;
}
