package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.CategoryRequest;
import com.moneta.wallet_service.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {

    List<CategoryResponse> getAllCategoriesByUserId(Long userId);

    CategoryResponse getCategoryById(Long id);

    CategoryResponse createCategory(CategoryRequest request, Long userId);

    void deleteCategory(Long id);

    List<CategoryResponse> getMandatoryCategories();
}