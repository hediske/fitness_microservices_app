package com.hediske.nutrition.controller;



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


    
}
