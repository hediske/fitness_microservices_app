package com.hediske.nutrition.services;

import com.hediske.nutrition.dto.FoodItemDto;
import com.hediske.nutrition.dto.MealEntryRequest;
import com.hediske.nutrition.dto.MealEntryResponse;
import com.hediske.nutrition.entities.Category;
import com.hediske.nutrition.entities.FoodItem;
import com.hediske.nutrition.entities.MealEntry;
import com.hediske.nutrition.repositories.CategoryRepository;
import com.hediske.nutrition.repositories.FoodItemRepository;
import com.hediske.nutrition.repositories.MealEntryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NutritionServiceImpl implements NutritionService {

    private final MealEntryRepository mealEntryRepository;
    private final FoodItemRepository foodItemRepository;
    private final CategoryRepository categoryRepository;

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
        return foodItemRepository.findByFilters(
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


}
