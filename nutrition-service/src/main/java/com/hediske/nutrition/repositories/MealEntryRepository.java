package com.hediske.nutrition.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.hediske.nutrition.entities.MealEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    List<MealEntry> findByUserEmail(String userEmail);
    List<MealEntry> findByUserEmailAndConsumedAtBetween(String userEmail, LocalDateTime start, LocalDateTime end);
}
