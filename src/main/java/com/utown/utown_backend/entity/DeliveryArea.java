package com.utown.utown_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "delivery_areas")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryArea extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String name;
}
