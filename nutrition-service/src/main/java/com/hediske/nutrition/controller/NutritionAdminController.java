package com.hediske.nutrition.controller;


import com.fitness.hediske.services.NutritionService;
import com.hediske.nutrition.services.NutritionService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/nutrition/admin")
@RequiredArgsConstructor
public class NutritionAdminController {

    private final NutritionService nutritionService;

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getNutritionStatistics() {
        Map<String, Object> stats = nutritionService.getNutritionStatistics();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/reset-database")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> resetNutritionDatabase() {
        nutritionService.resetDatabase();
        return ResponseEntity.ok("Nutrition database has been reset successfully.");
    }

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addNutritionCategory(@RequestParam String categoryName) {
        nutritionService.addNutritionCategory(categoryName);
        return ResponseEntity.ok("Category '" + categoryName + "' added successfully.");
    }
}
