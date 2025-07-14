package com.hediske.nutrition.repositories;

import com.hediske.nutrition.entities.Category;
import com.hediske.nutrition.entities.FoodItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByNameContainingIgnoreCase(String name);
    Page<FoodItem> findByCategoryNameIgnoreCase(String categoryName, Pageable page);
    List<FoodItem> findByCategory(Category category);
}
