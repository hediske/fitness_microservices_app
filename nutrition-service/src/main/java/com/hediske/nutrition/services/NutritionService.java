package com.hediske.nutrition.services;

import com.hediske.nutrition.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface NutritionService {

    // -----------------------------------
    // 1. MEAL ENTRY MANAGEMENT (User)
    // -----------------------------------

    MealEntryResponse addMeal(String userEmail, MealEntryRequest request);

    List<MealEntryResponse> getAllMeals(String userEmail);

    List<MealEntryResponse> getMealsByDateRange(String userEmail, LocalDateTime start, LocalDateTime end);

    List<MealEntryResponse> getMealsByDay(String userEmail, LocalDate date);

    void deleteMealEntry(Long entryId, String userEmail);

    void updateMealEntry(Long entryId, String userEmail, MealEntryRequest request);


    // -----------------------------------
    // 2. FOOD CATALOG MANAGEMENT
    // -----------------------------------

    // == Admin-only write ==
    FoodItemDto addFoodItem(FoodItemDto dto);

    void updateFoodItem(Long id, FoodItemDto dto);

    void deleteFoodItem(Long id);


    // == Shared (Admin + Users) ==

    List<FoodItemDto> getAllFoodItems();

    Page<FoodItemDto> getFoodItemsPaginated(Pageable pageable);

    FoodItemDto getFoodItemById(Long id);

    List<FoodItemDto> searchFoodItems(String name); // simple name search

    Page<FoodItemDto> searchFoodItemsWithFilters(
            String name,
            String category,
            Integer minCalories,
            Integer maxCalories,
            Integer minProtein,
            Integer maxProtein,
            Integer minCarbs,
            Integer maxCarbs,
            Integer minFats,
            Integer maxFats,
            Pageable pageable
    );

    String getFoodItemAvatarUrl(Long foodItemId); // e.g., CDN / S3 / static link

    // -----------------------------------
    // 3. CATEGORY MANAGEMENT
    // -----------------------------------

    CategoryDto createCategory(CategoryDto dto); // Admin
    List<CategoryDto> getAllCategories();

    Page<FoodItemDto> getFoodItemsByCategory(String categoryName, Pageable pageable);

    void deleteCategory(String categoryName); // Admin
    void updateCategory(String oldName, CategoryDto newData); // Admin


    // -----------------------------------
    // 4. STATISTICS & DASHBOARD
    // -----------------------------------

    // === Admin Dashboard ===
    Map<String, Object> getGlobalNutritionStats(); // total food items, avg macros, top 10 meals, etc.

    // === User Dashboard ===
    UserStatsDto getUserWeeklyStats(String userEmail);
    UserStatsDto getUserMonthlyStats(String userEmail);
}
