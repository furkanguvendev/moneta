package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.UserRequest;
import com.moneta.wallet_service.dto.response.UserResponse;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.exception.BaseException;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.repository.UserRepository;
import com.moneta.wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new BaseException("Bu e-posta adresi zaten kullanımda: " + request.email(), HttpStatus.CONFLICT);
        }
        if (userRepository.existsByUserName(request.userName())) {
            throw new BaseException("Bu kullanıcı adı zaten alınmış: " + request.userName(), HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPassword(request.password());
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
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı. ID: " + id));
    }

    @Override
    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Silinmek istenen kullanıcı bulunamadı. ID: " + userId);
        }
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