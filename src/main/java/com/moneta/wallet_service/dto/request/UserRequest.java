package com.moneta.wallet_service.dto.request;

public record UserRequest(
        String userName,
        String email,
        String password,
        String firstName,
        String lastName
) {}
