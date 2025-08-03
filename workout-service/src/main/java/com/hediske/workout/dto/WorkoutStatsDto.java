package com.hediske.workout.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.Map;

import com.hediske.workout.enums.MuscleGroup;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutStatsDto {
    private int totalSessions;
    private int totalExercises;
    private Map<MuscleGroup, Integer> muscleGroupDistribution;
    private Map<LocalDate, Integer> weeklyActivity;
    private Map<LocalDate, Integer> monthlyActivity;
}