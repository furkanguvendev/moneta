package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.entity.Role;
import com.moneta.wallet_service.enums.RoleType;
import com.moneta.wallet_service.exception.ResourceNotFoundException; // Kendi sınıfımız
import com.moneta.wallet_service.repository.RoleRepository;
import com.moneta.wallet_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role findByType(RoleType type) {
        return roleRepository.findByRoleType(type)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hata: " + type + " rolü veritabanında bulunamadı! " +
                                "Lütfen database'e '" + type + "' rolünü eklediğinizden emin olun."
                ));
    }
}