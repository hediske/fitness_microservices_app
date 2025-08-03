
package com.hediske.workout.repositories;

import com.hediske.workout.entities.WorkoutSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    Page<WorkoutSession> findByUserEmail(String userEmail, Pageable pageable);
    List<WorkoutSession> findByUserEmailAndStartTimeBetween(String userEmail, LocalDateTime start, LocalDateTime end);
}
