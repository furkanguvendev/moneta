package com.moneta.wallet_service.dto.response;

public record UserResponse(
        Long id,
        String userName,
        String email,
        String firstName,
        String lastName,
        Integer budgetStartDay,
        int walletCount
) {}