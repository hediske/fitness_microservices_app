package com.hediske.nutrition.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private LocalDateTime start;
    private LocalDateTime end;
}
