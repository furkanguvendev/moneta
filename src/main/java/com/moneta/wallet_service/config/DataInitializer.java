package com.moneta.wallet_service.config;

import com.moneta.wallet_service.entity.Role;
import com.moneta.wallet_service.enums.RoleType;
import com.moneta.wallet_service.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

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
        long count = roleRepository.count();
        System.out.println("VERİTABANI KONTROLÜ: Toplam rol sayısı = " + count);
    }
}