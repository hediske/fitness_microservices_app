
package com.hediske.workout.controller;

import com.hediske.workout.dto.ExerciseRecordDto;
import com.hediske.workout.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercise/records")
@RequiredArgsConstructor
public class ExerciseRecordController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseRecordDto> addExerciseRecord(
            @RequestBody ExerciseRecordDto dto,
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(exerciseService.addExerciseRecord(dto, userEmail));
    }

    @PutMapping("/{recordId}")
    public ResponseEntity<ExerciseRecordDto> updateExerciseRecord(
            @PathVariable Long recordId,
            @RequestBody ExerciseRecordDto dto,
            @RequestHeader("X-User-Email") String userEmail) {
        return ResponseEntity.ok(exerciseService.updateExerciseRecord(recordId, dto, userEmail));
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteExerciseRecord(
            @PathVariable Long recordId,
            @RequestHeader("X-User-Email") String userEmail) {
        exerciseService.deleteExerciseRecord(recordId, userEmail);
        return ResponseEntity.noContent().build();
    }
}
