package com.moneta.wallet_service.config;

import com.moneta.wallet_service.entity.Category;
import com.moneta.wallet_service.entity.Role;
import com.moneta.wallet_service.enums.RoleType;
import com.moneta.wallet_service.repository.CategoryRepository;
import com.moneta.wallet_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final CategoryRepository categoryRepository;

    private record DefaultCategory(String name, boolean isMandatory) {}

    private static final List<DefaultCategory> DEFAULT_CATEGORIES = List.of(
            new DefaultCategory("Maaş", false),
            new DefaultCategory("Kira", true),
            new DefaultCategory("Fatura", true),
            new DefaultCategory("Beslenme", true),
            new DefaultCategory("Ulaşım", true),
            new DefaultCategory("Sağlık", true),
            new DefaultCategory("Eğlence", false),
            new DefaultCategory("Giyim", false),
            new DefaultCategory("Eğitim", false),
            new DefaultCategory("Diğer", false)
    );

    @Override
    public void run(String... args) {
        for (RoleType type : RoleType.values()) {
            if (roleRepository.findByRoleType(type).isEmpty()) {
                Role role = new Role();
                role.setRoleType(type);
                roleRepository.save(role);
                System.out.println("Sisteme yeni rol eklendi: " + type);
            }
        }
        long roleCount = roleRepository.count();
        System.out.println("VERİTABANI KONTROLÜ: Toplam rol sayısı = " + roleCount);

        for (DefaultCategory defaultCategory : DEFAULT_CATEGORIES) {
            if (!categoryRepository.existsByNameAndUserIdIsNull(defaultCategory.name())) {
                Category category = new Category();
                category.setName(defaultCategory.name());
                category.setMandatory(defaultCategory.isMandatory());
                category.setDefault(true);
                category.setUser(null);
                categoryRepository.save(category);
                System.out.println("Sisteme yeni varsayılan kategori eklendi: " + defaultCategory.name());
            }
        }
        long categoryCount = categoryRepository.count();
        System.out.println("VERİTABANI KONTROLÜ: Toplam kategori sayısı = " + categoryCount);
    }
}