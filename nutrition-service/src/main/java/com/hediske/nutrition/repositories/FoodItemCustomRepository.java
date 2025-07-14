package com.hediske.nutrition.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hediske.nutrition.entities.FoodItem;

public interface FoodItemCustomRepository {
    Page<FoodItem> findByFilters(
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
}
