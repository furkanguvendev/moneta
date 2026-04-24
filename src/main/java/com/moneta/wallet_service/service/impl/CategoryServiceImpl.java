package com.moneta.wallet_service.service.impl;

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
    public List<Category> getAllCategoriesByUserId(Long userId) {
        return categoryRepository.findGlobalAndUserCategories(userId);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori bulunamadı. ID: " + id));
    }

    @Override
    @Transactional
    public Category createCategory(Category category, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı."));

        boolean isDuplicate = categoryRepository.existsByNameAndUserIdIsNull(category.getName()) ||
                categoryRepository.existsByNameAndUserId(category.getName(), userId);

        if (isDuplicate) {
            throw new RuntimeException("Bu isimde bir kategori zaten mevcut.");
        }

        category.setUser(user);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        if (category.getUser() == null || category.isMandatory()) {
            throw new RuntimeException("Sistem kategorileri silinemez!");
        }

        categoryRepository.delete(category);
    }

    @Override
    public List<Category> getMandatoryCategories() {
        return categoryRepository.findByIsMandatoryTrue();
    }
}