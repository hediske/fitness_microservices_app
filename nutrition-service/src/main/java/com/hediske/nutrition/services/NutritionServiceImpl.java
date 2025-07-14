package com.hediske.nutrition.services;

import com.hediske.nutrition.dto.CategoryDto;
import com.hediske.nutrition.dto.FoodItemDto;
import com.hediske.nutrition.dto.MealEntryRequest;
import com.hediske.nutrition.dto.MealEntryResponse;
import com.hediske.nutrition.dto.UserStatsDto;
import com.hediske.nutrition.entities.Category;
import com.hediske.nutrition.entities.FoodItem;
import com.hediske.nutrition.entities.MealEntry;
import com.hediske.nutrition.repositories.CategoryRepository;
import com.hediske.nutrition.repositories.FoodItemCustomRepository;
import com.hediske.nutrition.repositories.FoodItemRepository;
import com.hediske.nutrition.repositories.MealEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private final MealEntryRepository mealEntryRepository;
    private final FoodItemRepository foodItemRepository;
    private final CategoryRepository categoryRepository;
    private final FoodItemCustomRepository foodItemCustomRepository;

    @Override
    @Transactional
    public MealEntryResponse addMeal(String userEmail, MealEntryRequest request) {
        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new EntityNotFoundException("Food item not found"));

        MealEntry meal = MealEntry.builder()
                .userEmail(userEmail)
                .foodItem(foodItem)
                .consumedAt(request.getConsumedAt() != null ? request.getConsumedAt() : LocalDateTime.now())
                .build();

        MealEntry saved = mealEntryRepository.save(meal);
        return mapToResponse(saved);
    }

    @Override
    public List<MealEntryResponse> getAllMeals(String userEmail) {
        return mealEntryRepository.findByUserEmail(userEmail)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MealEntryResponse> getMealsByDateRange(String userEmail, LocalDateTime start, LocalDateTime end) {
        return mealEntryRepository.findByUserEmailAndConsumedAtBetween(userEmail, start, end)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MealEntryResponse> getMealsByDay(String userEmail, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return getMealsByDateRange(userEmail, start, end);
    }

    @Override
    @Transactional
    public void deleteMealEntry(Long entryId, String userEmail) {
        MealEntry entry = mealEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("Meal entry not found"));

        if (!entry.getUserEmail().equals(userEmail)) {
            throw new SecurityException("Unauthorized deletion attempt");
        }

        mealEntryRepository.delete(entry);
    }

    @Override
    @Transactional
    public void updateMealEntry(Long entryId, String userEmail, MealEntryRequest request) {
        MealEntry entry = mealEntryRepository.findById(entryId)
                .orElseThrow(() -> new EntityNotFoundException("Meal entry not found"));

        if (!entry.getUserEmail().equals(userEmail)) {
            throw new SecurityException("Unauthorized update attempt");
        }

        FoodItem foodItem = foodItemRepository.findById(request.getFoodItemId())
                .orElseThrow(() -> new EntityNotFoundException("Food item not found"));

        entry.setFoodItem(foodItem);
        entry.setConsumedAt(request.getConsumedAt() != null ? request.getConsumedAt() : entry.getConsumedAt());

        mealEntryRepository.save(entry);
    }



    @Override
    @Transactional
    public FoodItemDto addFoodItem(FoodItemDto dto) {
        FoodItem item = FoodItem.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .carbs(dto.getCarbs())
                .fats(dto.getFats())
                .imageUrl(dto.getImageUrl())
                .build();

        if (dto.getCategoryName() != null) {
            Category category = categoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + dto.getCategoryName()));
            item.setCategory(category);
        }

        FoodItem saved = foodItemRepository.save(item);
        return mapToDto(saved);
    }

    @Override
    @Transactional
    public void updateFoodItem(Long id, FoodItemDto dto) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food item not found with id: " + id));

        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setCalories(dto.getCalories());
        item.setProtein(dto.getProtein());
        item.setCarbs(dto.getCarbs());
        item.setFats(dto.getFats());
        item.setImageUrl(dto.getImageUrl());

        if (dto.getCategoryName() != null) {
            Category category = categoryRepository.findByName(dto.getCategoryName())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found: " + dto.getCategoryName()));
            item.setCategory(category);
        } else {
            item.setCategory(null);
        }

        foodItemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteFoodItem(Long id) {
        if (!foodItemRepository.existsById(id)) {
            throw new EntityNotFoundException("Food item not found with id: " + id);
        }
        foodItemRepository.deleteById(id);
    }

    @Override
    public List<FoodItemDto> getAllFoodItems() {
        return foodItemRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FoodItemDto> getFoodItemsPaginated(Pageable pageable) {
        return foodItemRepository.findAll(pageable)
                .map(this::mapToDto);
    }

    @Override
    public FoodItemDto getFoodItemById(Long id) {
        FoodItem item = foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food item not found with id: " + id));
        return mapToDto(item);
    }

    @Override
    public List<FoodItemDto> searchFoodItems(String name) {
        return foodItemRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FoodItemDto> searchFoodItemsWithFilters(
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
    ) {
        return foodItemCustomRepository.findByFilters(
                name,
                category,
                minCalories,
                maxCalories,
                minProtein,
                maxProtein,
                minCarbs,
                maxCarbs,
                minFats,
                maxFats,
                pageable
        ).map(this::mapToDto);
    }

    @Override
    public String getFoodItemAvatarUrl(Long foodItemId) {
        FoodItem item = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new EntityNotFoundException("Food item not found"));
        return item.getImageUrl();
    }



    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalArgumentException("Category already exists");
        }
    
        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    
        return mapCategoryToDto(categoryRepository.save(category));
    }
    

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapCategoryToDto)
                .collect(Collectors.toList());
    }


    @Override
    public Page<FoodItemDto> getFoodItemsByCategory(String categoryName, Pageable pageable) {
        return foodItemRepository.findByCategoryNameIgnoreCase(categoryName, pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional
    public void deleteCategory(String categoryName) {
        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    
        if (!foodItemRepository.findByCategory(category).isEmpty()) {
            throw new IllegalStateException("Cannot delete category with associated food items");
        }
    
        categoryRepository.delete(category);
    }
    
    @Override
    @Transactional
    public void updateCategory(String oldName, CategoryDto newData) {
        Category category = categoryRepository.findByNameIgnoreCase(oldName)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    
        category.setName(newData.getName());
        category.setDescription(newData.getDescription());
    
        categoryRepository.save(category);
    }
    
    @Override
    public Map<String, Object> getGlobalNutritionStats() {
        Map<String, Object> stats = new HashMap<>();
    
        List<FoodItem> items = foodItemRepository.findAll();
    
        stats.put("totalFoodItems", items.size());
        stats.put("averageCalories", items.stream().mapToInt(FoodItem::getCalories).average().orElse(0));
        stats.put("averageProtein", items.stream().mapToInt(FoodItem::getProtein).average().orElse(0));
        stats.put("averageCarbs", items.stream().mapToInt(FoodItem::getCarbs).average().orElse(0));
        stats.put("averageFats", items.stream().mapToInt(FoodItem::getFats).average().orElse(0));
    
        val top10 = mealEntryRepository.findTopConsumedFoodItems(PageRequest.of(0, 10));
            top10.stream()
                .map(row -> {
                    if (row[0] == null || row[1] == null) {
                        return null; // Skip null rows
                    }
                    FoodItem item = (FoodItem) row[0];
                    Long count = (Long) row[1];
                    
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", item.getName());
                    map.put("usageCount", count);
                    map.put("category", item.getCategory() != null ? item.getCategory().getName() : null);
                    map.put("avatar", item.getImageUrl());

                    return map;
                })
                .collect(Collectors.toList());
    
        stats.put("top10ConsumedFoods", top10);
        return stats;
    }
    
    @Override
    public UserStatsDto getUserWeeklyStats(String userEmail) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);
        return computeUserStats(userEmail, oneWeekAgo, now);
    }
    
    @Override
    public UserStatsDto getUserMonthlyStats(String userEmail) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneMonthAgo = now.minusMonths(1);
        return computeUserStats(userEmail, oneMonthAgo, now);
    }
    
    private UserStatsDto computeUserStats(String userEmail, LocalDateTime from, LocalDateTime to) {
        List<MealEntry> meals = mealEntryRepository.findByUserEmailAndConsumedAtBetween(userEmail, from, to);
    
        int totalCalories = 0;
        int totalProtein = 0;
        int totalCarbs = 0;
        int totalFats = 0;
    
        for (MealEntry entry : meals) {
            FoodItem item = entry.getFoodItem();
            int quantity = entry.getQuantityInGrams() != null ? entry.getQuantityInGrams() : 100;
    
            totalCalories += item.getCalories() * quantity / 100;
            totalProtein += item.getProtein() * quantity / 100;
            totalCarbs += item.getCarbs() * quantity / 100;
            totalFats += item.getFats() * quantity / 100;
        }
    
        return UserStatsDto.builder()
                .totalCalories(totalCalories)
                .totalProtein(totalProtein)
                .totalCarbs(totalCarbs)
                .totalFats(totalFats)
                .totalMeals(meals.size())
                .start(from)
                .end(to)
                .build();
    }
    




    private FoodItemDto mapToDto(FoodItem item) {
        return FoodItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .calories(item.getCalories())
                .protein(item.getProtein())
                .carbs(item.getCarbs())
                .fats(item.getFats())
                .imageUrl(item.getImageUrl())
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .build();
    }


    private MealEntryResponse mapToResponse(MealEntry meal) {
        FoodItem item = meal.getFoodItem();

        if (item == null) {
            throw new EntityNotFoundException("Food item not found for meal entry");
        }

        FoodItemDto foodItemDto = FoodItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .calories(item.getCalories())
                .protein(item.getProtein())
                .carbs(item.getCarbs())
                .fats(item.getFats())
                .description(item.getDescription())
                .imageUrl(item.getImageUrl())
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .build();



        return MealEntryResponse.builder()
                .foodItem(foodItemDto)
                .quantityInGrams(meal.getQuantityInGrams())
                .consumedAt(meal.getConsumedAt())
                .build();
    }

    private CategoryDto mapCategoryToDto(Category category) {
        return CategoryDto.builder()
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
    


}
