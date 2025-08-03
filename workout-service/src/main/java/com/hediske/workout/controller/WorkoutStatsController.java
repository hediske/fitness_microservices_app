
package com.hediske.workout.controller;

import com.hediske.workout.dto.WorkoutStatsDto;
import com.hediske.workout.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/exercise/stats")
@RequiredArgsConstructor
public class WorkoutStatsController {

    private final ExerciseService exerciseService;

    @GetMapping
    public ResponseEntity<WorkoutStatsDto> getUserWorkoutStats(
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(exerciseService.getUserWorkoutStats(userEmail));
    }

    @GetMapping("/range")
    public ResponseEntity<WorkoutStatsDto> getUserWorkoutStatsBetween(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(exerciseService.getUserWorkoutStatsBetween(userEmail, start, end));
    }
}