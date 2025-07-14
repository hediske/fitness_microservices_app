package com.hediske.nutrition.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "meal_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;

    private Integer quantityInGrams;


    private LocalDateTime consumedAt;
}
