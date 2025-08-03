package com.hediske.workout.dto;

import com.hediske.workout.enums.ExerciseType;
import com.hediske.workout.enums.MuscleGroup;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDto {
    private Long id;
    private String name;
    private String description;
    private ExerciseType type;
    private MuscleGroup primaryMuscle;
    private Set<MuscleGroup> secondaryMuscles;
    private Integer defaultSets;
    private Integer defaultReps;
    private String videoUrl;
    private String imageUrl;
}