package com.hediske.nutrition.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealEntryRequest {
    private Long foodItemId;
    private Integer quantityInGrams;
    private LocalDateTime consumedAt;
}
