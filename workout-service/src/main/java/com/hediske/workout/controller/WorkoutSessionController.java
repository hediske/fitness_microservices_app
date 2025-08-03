
package com.hediske.workout.controller;

import com.hediske.workout.dto.*;
import com.hediske.workout.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/exercise/sessions")
@RequiredArgsConstructor
public class WorkoutSessionController {

    private final ExerciseService exerciseService;

    @PostMapping("/start")
    public ResponseEntity<WorkoutSessionDto> startSession(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime) {
        return ResponseEntity.ok(exerciseService.startSession(userEmail, startTime));
    }

    @PostMapping("/{sessionId}/end")
    public ResponseEntity<WorkoutSessionDto> endSession(
            @PathVariable Long sessionId,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return ResponseEntity.ok(exerciseService.endSession(sessionId, userEmail, endTime));
    }

    @GetMapping("/{sessionId}")
    public ResponseEntity<WorkoutSessionDetailDto> getSessionDetails(
            @PathVariable Long sessionId,
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(exerciseService.getSessionDetails(sessionId, userEmail));
    }

    @GetMapping
    public ResponseEntity<Page<WorkoutSessionDto>> getUserSessions(
            @RequestHeader("X-User-Email") String userEmail,
            Pageable pageable) {
        return ResponseEntity.ok(exerciseService.getUserSessions(userEmail, pageable));
    }

    @GetMapping("/range")
    public ResponseEntity<List<WorkoutSessionDto>> getUserSessionsBetween(
            @RequestHeader("X-User-Email") String userEmail,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(exerciseService.getUserSessionsBetween(userEmail, start, end));
    }
}
