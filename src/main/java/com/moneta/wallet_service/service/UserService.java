package com.moneta.wallet_service.service;

import com.moneta.wallet_service.entity.User;

public interface UserService {

    User createUser(User user);
    User getUserById(Long id);
}
