package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.UserRequest;
import com.moneta.wallet_service.dto.response.UserResponse;
import com.moneta.wallet_service.entity.User;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserProfile(Long id);
    User getUserById(Long id);
    void deleteUser(Long userId);
}
