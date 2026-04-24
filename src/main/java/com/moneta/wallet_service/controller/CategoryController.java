package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.entity.Category;
import com.moneta.wallet_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Category>> getAllCategories(@PathVariable Long userId) {
        return ResponseEntity.ok(categoryService.getAllCategoriesByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Category> createCategory(@RequestBody Category category, @PathVariable Long userId) {
        return ResponseEntity.ok(categoryService.createCategory(category, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mandatory")
    public ResponseEntity<List<Category>> getMandatoryCategories() {
        return ResponseEntity.ok(categoryService.getMandatoryCategories());
    }
}