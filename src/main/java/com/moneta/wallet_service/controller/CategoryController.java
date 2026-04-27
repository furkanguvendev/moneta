package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.CategoryRequest;
import com.moneta.wallet_service.dto.response.CategoryResponse;
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
    public ResponseEntity<List<CategoryResponse>> getAllCategories(@PathVariable Long userId) {
        return ResponseEntity.ok(categoryService.getAllCategoriesByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryRequest request, // Artık Category entity değil, Request DTO alıyoruz
            @PathVariable Long userId) {
        return ResponseEntity.ok(categoryService.createCategory(request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mandatory")
    public ResponseEntity<List<CategoryResponse>> getMandatoryCategories() {
        return ResponseEntity.ok(categoryService.getMandatoryCategories());
    }
}