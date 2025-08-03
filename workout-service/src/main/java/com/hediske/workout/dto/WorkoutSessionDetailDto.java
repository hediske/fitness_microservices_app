
package com.hediske.workout.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSessionDetailDto {
    private WorkoutSessionDto session;
    private List<ExerciseRecordDto> records;
}