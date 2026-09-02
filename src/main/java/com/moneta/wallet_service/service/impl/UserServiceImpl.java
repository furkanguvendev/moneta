package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.UserUpdateRequest;
import com.moneta.wallet_service.dto.response.UserResponse;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.mapper.UserMapper;
import com.moneta.wallet_service.repository.UserRepository;
import com.moneta.wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long id) {
        return userMapper.toResponse(getUserById(id));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı. ID: " + id));
    }

    @Override
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUserNameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + usernameOrEmail));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Silinmek istenen kullanıcı bulunamadı. ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserByUsernameOrEmailWithRoles(String usernameOrEmail) {
        return userRepository.findByUserNameOrEmailWithRoles(usernameOrEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + usernameOrEmail));
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(Long id, UserUpdateRequest request) {
        User user = getUserById(id);

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }
        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }
        if (request.budgetStartDay() != null) {
            user.setBudgetStartDay(request.budgetStartDay());
        }

        return userMapper.toResponse(userRepository.save(user));
    }
}