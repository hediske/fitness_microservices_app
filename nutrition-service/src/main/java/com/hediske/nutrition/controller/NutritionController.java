package com.hediske.nutrition.controller;

import com.hediske.nutrition.dto.MealEntryRequest;
import com.hediske.nutrition.dto.MealEntryResponse;
import com.hediske.nutrition.dto.UserStatsDto;
import com.hediske.nutrition.dto.FoodItemDto;
import com.hediske.nutrition.services.NutritionService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;

    @PostMapping("/meals")
    public ResponseEntity<MealEntryResponse> addMeal(@RequestHeader("X-User-Email") String email,
                                                     @RequestBody MealEntryRequest request) {
        return ResponseEntity.ok(nutritionService.addMeal(email, request));
    }

    @GetMapping("/meals")
    public ResponseEntity<List<MealEntryResponse>> getAllMeals(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(nutritionService.getAllMeals(email));
    }

    @GetMapping("/meals/range")
    public ResponseEntity<List<MealEntryResponse>> getMealsInRange(
            @RequestHeader("X-User-Email") String email,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(nutritionService.getMealsByDateRange(email, start, end));
    }

    @GetMapping("/meals/day")
    public ResponseEntity<List<MealEntryResponse>> getMealsByDay(
            @RequestHeader("X-User-Email") String email,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(nutritionService.getMealsByDay(email, date));
    }

    @DeleteMapping("/meals/{id}")
    public ResponseEntity<Void> deleteMeal(@PathVariable Long id,
                                           @RequestHeader("X-User-Email") String email) {
        nutritionService.deleteMealEntry(id, email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/meals/{id}")
    public ResponseEntity<Void> updateMeal(@PathVariable Long id,
                                           @RequestHeader("X-User-Email") String email,
                                           @RequestBody MealEntryRequest request) {
        nutritionService.updateMealEntry(id, email, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/week")
    public ResponseEntity<UserStatsDto> getWeeklyStats(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(nutritionService.getUserWeeklyStats(email));
    }

    @GetMapping("/stats/month")
    public ResponseEntity<UserStatsDto> getMonthlyStats(@RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(nutritionService.getUserMonthlyStats(email));
    }

    @GetMapping("/food/{id}/avatar")
    public ResponseEntity<String> getFoodItemAvatar(@PathVariable Long id) {
        return ResponseEntity.ok(nutritionService.getFoodItemAvatarUrl(id));
    }
}
