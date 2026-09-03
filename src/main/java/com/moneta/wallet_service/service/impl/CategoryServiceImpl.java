package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.CategoryRequest;
import com.moneta.wallet_service.dto.response.CategoryResponse;
import com.moneta.wallet_service.entity.Category;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.exception.BaseException;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.mapper.CategoryMapper;
import com.moneta.wallet_service.repository.CategoryRepository;
import com.moneta.wallet_service.repository.UserRepository;
import com.moneta.wallet_service.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final CategoryMapper categoryMapper;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUserNameOrEmail(email, email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + email));
    }

    @Override
    public List<CategoryResponse> getAllCategoriesByUserId(Long userId) {
        User authUser = getAuthenticatedUser();
        List<Category> categories = categoryRepository.findGlobalAndUserCategories(authUser.getId());
        return categories.stream().map(categoryMapper::toResponse).toList();
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        Category category = getCategoryEntityById(id);
        return categoryMapper.toResponse(category);
    }

    @Override
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı. ID: " + id));
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request, Long userId) {
        User authUser = getAuthenticatedUser();

        boolean isDuplicate = categoryRepository.existsByNameAndUserIdIsNull(request.name()) ||
                categoryRepository.existsByNameAndUserId(request.name(), authUser.getId());

        if (isDuplicate) {
            throw new BaseException("Bu isimde bir kategori zaten mevcut: " + request.name(), HttpStatus.CONFLICT);
        }

        Category category = categoryMapper.toEntity(request);
        category.setUser(authUser);
        category.setDefault(false);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryEntityById(id);

        if (category.isDefault()) {
            throw new BaseException("Sistem varsayılan kategorileri silinemez!", HttpStatus.BAD_REQUEST);
        }

        User authUser = getAuthenticatedUser();
        if (category.getUser() == null || !category.getUser().getId().equals(authUser.getId())) {
            throw new AccessDeniedException("Bu kategoriyi silme yetkiniz yok!");
        }

        categoryRepository.delete(category);
    }

    @Override
    public List<CategoryResponse> getMandatoryCategories() {
        List<Category> categories = categoryRepository.findByIsMandatoryTrue();
        return categories.stream().map(categoryMapper::toResponse).toList();
    }
}