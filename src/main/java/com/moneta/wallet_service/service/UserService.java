package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.response.UserResponse;
import com.moneta.wallet_service.entity.User;

public interface UserService {
    UserResponse getUserProfile(Long id);
    User getUserById(Long id);
    User getUserByUsernameOrEmail(String usernameOrEmail);
    void deleteUser(Long userId);
}