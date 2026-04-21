package com.moneta.wallet_service.service;

import com.moneta.wallet_service.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getAllCategoriesByUserId(Long userId);

    Category getCategoryById(Long id);

    Category createCategory(Category category, Long userId);

    void deleteCategory(Long id, Long userId);

    List<Category> getMandatoryCategories();
}
