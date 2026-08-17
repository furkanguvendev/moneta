package com.moneta.wallet_service.mapper;

import com.moneta.wallet_service.dto.request.CategoryRequest;
import com.moneta.wallet_service.dto.response.CategoryResponse;
import com.moneta.wallet_service.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryRequest request) {
        if (request == null) return null;

        Category category = new Category();
        category.setName(request.name());
        category.setMandatory(request.isMandatory());
        return category;
    }

    public CategoryResponse toResponse(Category entity) {
        if (entity == null) return null;

        return new CategoryResponse(
                entity.getId(),
                entity.getName(),
                entity.isMandatory(),
                entity.isDefault()
        );
    }
}