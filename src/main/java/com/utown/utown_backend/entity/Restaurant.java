package com.utown.utown_backend.entity;

import com.utown.utown_backend.enums.RestaurantStatus;
import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "restaurants")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_category_id", nullable = false)
    private RestaurantCategory category;

    @Column(nullable = false)
    private String name;

    private String description;

    private String imageUrl;

    @Column(nullable = false)
    private Double minimumOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RestaurantStatus status;
}