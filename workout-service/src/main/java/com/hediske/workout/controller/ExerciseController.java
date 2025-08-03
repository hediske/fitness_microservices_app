package com.hediske.workout.controller;

import com.hediske.workout.dto.*;
import com.hediske.workout.enums.ExerciseType;
import com.hediske.workout.enums.MuscleGroup;
import com.hediske.workout.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/exercise")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseDto> createExercise(@RequestBody ExerciseDto dto) {
        return ResponseEntity.ok(exerciseService.createExercise(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ExerciseDto> updateExercise(@PathVariable Long id, @RequestBody ExerciseDto dto) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ExerciseDto>> getAllExercises(Pageable pageable) {
        return ResponseEntity.ok(exerciseService.getAllExercises(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDto> getExerciseById(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ExerciseDto>> searchExercises(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ExerciseType type,
            @RequestParam(required = false) MuscleGroup muscle,
            Pageable pageable) {
        return ResponseEntity.ok(exerciseService.searchExercises(name, type, muscle, pageable));
    }
}
