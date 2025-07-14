package com.hediske.nutrition.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodItemDto {
    private Long id;
    private String name;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fats;
    private String description;
    private String imageUrl;
    private String categoryName;

}
