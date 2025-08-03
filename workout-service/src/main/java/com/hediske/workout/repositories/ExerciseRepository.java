package com.hediske.workout.repositories;

import com.hediske.workout.entities.Exercise;
import com.hediske.workout.enums.ExerciseType;
import com.hediske.workout.enums.MuscleGroup;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Page<Exercise> findByType(ExerciseType type, Pageable pageable);

    Page<Exercise> findByPrimaryMuscle(MuscleGroup muscleGroup, Pageable pageable);

    Page<Exercise> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT e FROM Exercise e WHERE :muscle MEMBER OF e.secondaryMuscles")
    Page<Exercise> findBySecondaryMuscle(@Param("muscle") MuscleGroup muscle, Pageable pageable);

    @Query("SELECT e FROM Exercise e WHERE e.type = :type AND :muscle MEMBER OF e.secondaryMuscles")
    Page<Exercise> findByTypeAndSecondaryMuscle(
            @Param("type") ExerciseType type,
            @Param("muscle") MuscleGroup muscle,
            Pageable pageable);

    Page<Exercise> findByNameContainingIgnoreCaseAndType(String name, ExerciseType type, Pageable pageable);

    Page<Exercise> findByNameContainingIgnoreCaseAndTypeAndPrimaryMuscle(String name, ExerciseType type,
            MuscleGroup muscle, Pageable pageable);

    Page<Exercise> findByNameContainingIgnoreCaseAndPrimaryMuscle(String name, MuscleGroup muscle,
            Pageable pageable);

    Page<Exercise> findByTypeAndPrimaryMuscle(ExerciseType type, MuscleGroup muscle, Pageable pageable);

}
