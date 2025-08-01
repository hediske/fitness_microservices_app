package com.hediske.nutrition.controller;

import com.hediske.nutrition.dto.CategoryDto;
import com.hediske.nutrition.dto.FoodItemDto;
import com.hediske.nutrition.services.NutritionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nutrition/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") 
public class NutritionAdminController {

    private final NutritionService nutritionService;

    // --- FOOD ITEMS ---

    @PostMapping("/food-items")
    public ResponseEntity<FoodItemDto> addFoodItem(@RequestBody FoodItemDto dto) {
        FoodItemDto created = nutritionService.addFoodItem(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/food-items/{id}")
    public ResponseEntity<Void> updateFoodItem(@PathVariable Long id, @RequestBody FoodItemDto dto) {
        nutritionService.updateFoodItem(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/food-items/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        nutritionService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/food-items")
    public ResponseEntity<List<FoodItemDto>> getAllFoodItems() {
        List<FoodItemDto> list = nutritionService.getAllFoodItems();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/food-items/paginated")
    public ResponseEntity<Page<FoodItemDto>> getFoodItemsPaginated(Pageable pageable) {
        Page<FoodItemDto> page = nutritionService.getFoodItemsPaginated(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/food-items/search")
    public ResponseEntity<List<FoodItemDto>> searchFoodItems(@RequestParam String name) {
        List<FoodItemDto> results = nutritionService.searchFoodItems(name);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/food-items/search-filtered")
    public ResponseEntity<Page<FoodItemDto>> searchFoodItemsWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer minCalories,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) Integer minProtein,
            @RequestParam(required = false) Integer maxProtein,
            @RequestParam(required = false) Integer minCarbs,
            @RequestParam(required = false) Integer maxCarbs,
            @RequestParam(required = false) Integer minFats,
            @RequestParam(required = false) Integer maxFats,
            Pageable pageable) {
        Page<FoodItemDto> results = nutritionService.searchFoodItemsWithFilters(
                name, category,
                minCalories, maxCalories,
                minProtein, maxProtein,
                minCarbs, maxCarbs,
                minFats, maxFats,
                pageable
        );
        return ResponseEntity.ok(results);
    }

    @GetMapping("/food-items/category/{categoryName}")
    public ResponseEntity<Page<FoodItemDto>> getFoodItemsByCategory(@PathVariable String categoryName, Pageable pageable) {
        Page<FoodItemDto> page = nutritionService.getFoodItemsByCategory(categoryName, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/food-items/{id}/avatar")
    public ResponseEntity<String> getFoodItemAvatar(@PathVariable Long id) {
        String url = nutritionService.getFoodItemAvatarUrl(id);
        return ResponseEntity.ok(url);
    }

    // --- CATEGORIES ---

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto dto) {
        CategoryDto created = nutritionService.createCategory(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = nutritionService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/categories/{categoryName}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryName) {
        nutritionService.deleteCategory(categoryName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/categories/{oldName}")
    public ResponseEntity<Void> updateCategory(@PathVariable String oldName, @RequestBody CategoryDto newData) {
        nutritionService.updateCategory(oldName, newData);
        return ResponseEntity.noContent().build();
    }

    // --- STATISTICS ---

    @GetMapping("/stats/global")
    public ResponseEntity<Map<String, Object>> getGlobalNutritionStats() {
        Map<String, Object> stats = nutritionService.getGlobalNutritionStats();
        return ResponseEntity.ok(stats);
    }
}
