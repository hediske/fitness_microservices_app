package com.hediske.nutrition.repositories;
import com.hediske.nutrition.entities.FoodItem;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FoodItemCustomRepositoryImpl implements FoodItemCustomRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<FoodItem> findByFilters(String name, String category, Integer minCalories, Integer maxCalories,
                                        Integer minProtein, Integer maxProtein, Integer minCarbs, Integer maxCarbs,
                                        Integer minFats, Integer maxFats, Pageable pageable) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<FoodItem> query = cb.createQuery(FoodItem.class);
        Root<FoodItem> root = query.from(FoodItem.class);
        Join<Object, Object> categoryJoin = root.join("category", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        if (name != null && !name.isBlank())
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));

        if (category != null && !category.isBlank())
            predicates.add(cb.equal(cb.lower(categoryJoin.get("name")), category.toLowerCase()));

        if (minCalories != null) predicates.add(cb.ge(root.get("calories"), minCalories));
        if (maxCalories != null) predicates.add(cb.le(root.get("calories"), maxCalories));

        if (minProtein != null) predicates.add(cb.ge(root.get("protein"), minProtein));
        if (maxProtein != null) predicates.add(cb.le(root.get("protein"), maxProtein));

        if (minCarbs != null) predicates.add(cb.ge(root.get("carbs"), minCarbs));
        if (maxCarbs != null) predicates.add(cb.le(root.get("carbs"), maxCarbs));

        if (minFats != null) predicates.add(cb.ge(root.get("fats"), minFats));
        if (maxFats != null) predicates.add(cb.le(root.get("fats"), maxFats));

        query.where(cb.and(predicates.toArray(new Predicate[0])));
        query.orderBy(cb.asc(root.get("name")));

        // Fetch results
        List<FoodItem> resultList = em.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // Total count query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<FoodItem> countRoot = countQuery.from(FoodItem.class);
        Join<Object, Object> countCategoryJoin = countRoot.join("category", JoinType.LEFT);
        countQuery.select(cb.count(countRoot)).where(cb.and(predicates.toArray(new Predicate[0])));
        Long totalCount = em.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(resultList, pageable, totalCount);
    }
}
