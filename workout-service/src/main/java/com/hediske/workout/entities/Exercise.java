package com.hediske.workout.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.hediske.workout.enums.ExerciseType;
import com.hediske.workout.enums.MuscleGroup;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    private ExerciseType type;

    @Enumerated(EnumType.STRING)
    private MuscleGroup primaryMuscle;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<MuscleGroup> secondaryMuscles = new HashSet<>();

    @Column(nullable = false)
    private Integer defaultSets;

    @Column(nullable = false)
    private Integer defaultReps;

    private String videoUrl;
    private String imageUrl;
}
