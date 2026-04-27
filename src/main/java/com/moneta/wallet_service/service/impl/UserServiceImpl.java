package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.UserRequest;
import com.moneta.wallet_service.dto.response.UserResponse;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.repository.UserRepository;
import com.moneta.wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    // Not: Security eklediğinde buraya BCryptPasswordEncoder gelecek

    @Override
    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPassword(request.password()); // Gerçek projede encode edilmeli
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        return convertToResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserProfile(Long id) {
        return convertToResponse(getUserById(id));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı. ID: " + id));
    }

    @Override
    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId)) throw new RuntimeException("Kullanıcı yok.");
        userRepository.deleteById(userId);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName() + " " + user.getLastName(),
                user.getWallets() != null ? user.getWallets().size() : 0
        );
    }
}
