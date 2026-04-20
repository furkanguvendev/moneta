package com.moneta.wallet_service.service;

import com.moneta.wallet_service.entity.Category;

import java.util.List;

public interface CategoryService {

    // Listeleme: Sistem + Kullanıcıya özel olanlar
    List<Category> getAllCategoriesByUserId(Long userId);

    // Tekil getirme
    Category getCategoryById(Long id);

    // Ekleme
    Category createCategory(Category category, Long userId);

    // Silme (Güvenlik kontrolleri servis içinde yapılacak)
    void deleteCategory(Long id, Long userId);

    // Opsiyonel: Sadece zorunlu kategorileri getir (Raporlama için kolaylık sağlar)
    List<Category> getMandatoryCategories();
}
