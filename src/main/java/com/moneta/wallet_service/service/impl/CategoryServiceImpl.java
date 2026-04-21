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

        if (categoryRepository.existsByNameAndUserIdIsNull(category.getName()) ||
                categoryRepository.existsByNameAndUserId(category.getName(), userId)) {
            throw new RuntimeException("Bu isimde bir kategori zaten mevcut (Sistemde veya sizde).");
        }

        user.addCategory(category);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, Long userId) {
        Category category = getCategoryById(id);

        if (category.getUser() == null) {
            throw new RuntimeException("Sistem kategorileri silinemez!");
        }

        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu kategoriyi silme yetkiniz yok.");
        }

        categoryRepository.delete(category);
    }

    @Override
    public List<Category> getMandatoryCategories() {
        return categoryRepository.findByIsMandatoryTrue();
    }
}