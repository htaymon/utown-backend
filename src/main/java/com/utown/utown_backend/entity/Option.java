package com.utown.utown_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "options")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Option extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dish_id", nullable = false)
    private Dish dish;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double extraPrice;

    @OneToMany(mappedBy = "option",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<OrderItemOption> orderItemOptions;
}
