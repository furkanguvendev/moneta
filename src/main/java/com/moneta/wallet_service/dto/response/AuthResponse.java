package com.moneta.wallet_service.dto.response;

public record AuthResponse(
        Long userId,
        String username,
        String email,
        String token,
        String tokenType
) {
    public AuthResponse(Long userId, String username, String email, String token) {
        this(userId, username, email, token, "Bearer");
    }
}