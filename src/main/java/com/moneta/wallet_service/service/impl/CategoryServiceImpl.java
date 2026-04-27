package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.CategoryRequest;
import com.moneta.wallet_service.dto.response.CategoryResponse;
import com.moneta.wallet_service.entity.Category;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.repository.CategoryRepository;
import com.moneta.wallet_service.repository.UserRepository;
import com.moneta.wallet_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public List<CategoryResponse> getAllCategoriesByUserId(Long userId) {
        return categoryRepository.findGlobalAndUserCategories(userId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı. ID: " + id));

        return convertToResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        boolean isDuplicate = categoryRepository.existsByNameAndUserIdIsNull(request.name()) ||
                categoryRepository.existsByNameAndUserId(request.name(), userId);

        if (isDuplicate) {
            throw new RuntimeException("Bu isimde bir kategori zaten mevcut.");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setUser(user);

        category.setMandatory(request.isMandatory());

        category.setDefault(false);

        return convertToResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı."));

        if (category.isDefault()) {
            throw new RuntimeException("Sistem varsayılan kategorileri silinemez!");
        }

        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponse> getMandatoryCategories() {
        return categoryRepository.findByIsMandatoryTrue()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    private CategoryResponse convertToResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.isMandatory(),
                category.isDefault()
        );
    }
}