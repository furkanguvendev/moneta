package com.moneta.wallet_service.service;

import com.moneta.wallet_service.entity.Role;
import com.moneta.wallet_service.enums.RoleType;

public interface RoleService {
    Role findByType(RoleType type);
}