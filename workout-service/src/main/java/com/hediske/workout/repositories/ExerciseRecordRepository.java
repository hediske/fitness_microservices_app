
package com.hediske.workout.repositories;

import com.hediske.workout.entities.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    List<ExerciseRecord> findBySessionId(Long sessionId);
}