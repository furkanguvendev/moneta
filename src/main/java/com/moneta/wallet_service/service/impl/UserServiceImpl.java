package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.repository.UserRepository;
import com.moneta.wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(()->new RuntimeException("Kullanıcı Bulunamadı."));
    }
}
