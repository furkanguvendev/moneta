package com.moneta.wallet_service.mapper;

import com.moneta.wallet_service.dto.request.UserRequest;
import com.moneta.wallet_service.dto.response.UserResponse;
import com.moneta.wallet_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setBudgetStartDay(request.budgetStartDay() != null ? request.budgetStartDay() : 1);
        return user;
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) return null;

        int walletCount = entity.getWallets() != null ? entity.getWallets().size() : 0;

        return new UserResponse(
                entity.getId(),
                entity.getUserName(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getBudgetStartDay(),
                walletCount
        );
    }
}