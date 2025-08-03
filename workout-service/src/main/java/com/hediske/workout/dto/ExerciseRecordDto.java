
package com.hediske.workout.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseRecordDto {
    private Long id;
    private Long sessionId;
    private Long exerciseId;
    private Integer sets;
    private Integer reps;
    private Double weight;
    private String notes;
}
