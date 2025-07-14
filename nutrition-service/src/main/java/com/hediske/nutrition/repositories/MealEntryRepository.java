package com.hediske.nutrition.repositories;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hediske.nutrition.entities.MealEntry;

import java.time.LocalDateTime;
import java.util.List;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    List<MealEntry> findByUserEmail(String userEmail);
    List<MealEntry> findByUserEmailAndConsumedAtBetween(String userEmail, LocalDateTime start, LocalDateTime end);


    @Query("SELECT m.foodItem, COUNT(m) as usageCount " +
       "FROM MealEntry m " +
       "GROUP BY m.foodItem " +
       "ORDER BY usageCount DESC")
    List<Object[]> findTopConsumedFoodItems(Pageable pageable);

}
