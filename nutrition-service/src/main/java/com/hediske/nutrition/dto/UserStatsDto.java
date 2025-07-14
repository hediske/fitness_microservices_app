package com.hediske.nutrition.dto;

import java.time.LocalDate;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsDto {
    private int totalMeals;
    private int totalCalories;
    private int totalProtein;
    private int totalCarbs;
    private int totalFats;
    private LocalDate start;
    private LocalDate end;
}
