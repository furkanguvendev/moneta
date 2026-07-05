package com.moneta.wallet_service.dto.response;

public record LoginResponse(
        Long id,
        String username,
        String email,
        String token,
        String message
) { }