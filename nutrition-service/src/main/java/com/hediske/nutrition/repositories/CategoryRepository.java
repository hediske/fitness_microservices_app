package com.hediske.nutrition.repositories;

import com.hediske.nutrition.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    Optional<Category> findByNameIgnoreCase(String name);
    Boolean existsByNameIgnoreCase(String name);
}
