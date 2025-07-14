package com.hediske.nutrition.controller;


import com.hediske.nutrition.dto.MealEntryRequest;
import com.hediske.nutrition.dto.MealEntryResponse;
import com.hediske.nutrition.services.NutritionService;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionService nutritionService;

    private static final String USER_EMAIL_HEADER = "X-User-Email";

    @PostMapping("/meals")
    public ResponseEntity<MealEntryResponse> addMeal(
            @RequestHeader(USER_EMAIL_HEADER) String userEmail,
            @RequestBody MealEntryRequest request) {

        MealEntryResponse response = nutritionService.addMeal(userEmail, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/meals")
    public ResponseEntity<List<MealEntryResponse>> getAllMeals(
            @RequestHeader(USER_EMAIL_HEADER) String userEmail) {

        List<MealEntryResponse> meals = nutritionService.getAllMeals(userEmail);
        return ResponseEntity.ok(meals);
    }

    @GetMapping("/meals/date-range")
    public ResponseEntity<List<MealEntryResponse>> getMealsByDateRange(
            @RequestHeader(USER_EMAIL_HEADER) String userEmail,
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<MealEntryResponse> meals = nutritionService.getMealsByDateRange(userEmail, start, end);
        return ResponseEntity.ok(meals);
    }
}
