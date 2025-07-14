package com.hediske.nutrition.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealEntryResponse {
    private FoodItemDto foodItem;
    private Integer quantityInGrams;
    private LocalDateTime consumedAt;
}
