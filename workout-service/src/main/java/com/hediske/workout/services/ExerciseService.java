package com.hediske.workout.services;

import com.hediske.workout.dto.*;
import com.hediske.workout.enums.ExerciseType;
import com.hediske.workout.enums.MuscleGroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface ExerciseService {

    // Exercise Management
    ExerciseDto createExercise(ExerciseDto dto);

    ExerciseDto updateExercise(Long id, ExerciseDto dto);

    void deleteExercise(Long id);

    Page<ExerciseDto> getAllExercises(Pageable pageable);

    ExerciseDto getExerciseById(Long id);

    Page<ExerciseDto> searchExercises(String name, ExerciseType type, MuscleGroup muscle, Pageable pageable);

    // Workout Session Management
    WorkoutSessionDto startSession(String userEmail, LocalDateTime startTime);

    WorkoutSessionDto endSession(Long sessionId, String userEmail, LocalDateTime endTime);

    WorkoutSessionDetailDto getSessionDetails(Long sessionId, String userEmail);

    Page<WorkoutSessionDto> getUserSessions(String userEmail, Pageable pageable);

    List<WorkoutSessionDto> getUserSessionsBetween(String userEmail, LocalDateTime start, LocalDateTime end);

    // Exercise Records
    ExerciseRecordDto addExerciseRecord(ExerciseRecordDto dto, String userEmail);

    ExerciseRecordDto updateExerciseRecord(Long recordId, ExerciseRecordDto dto, String userEmail);

    void deleteExerciseRecord(Long recordId, String userEmail);

    // Statistics
    WorkoutStatsDto getUserWorkoutStats(String userEmail);

    WorkoutStatsDto getUserWorkoutStatsBetween(String userEmail, LocalDateTime start, LocalDateTime end);
}