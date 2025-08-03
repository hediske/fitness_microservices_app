package com.hediske.workout.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSessionDto {
    private Long id;
    private String userEmail;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
}
