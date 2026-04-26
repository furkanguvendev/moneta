package com.moneta.wallet_service.dto.response;

public record LoginResponse(
        String username,
        String email,
        String token,
        String message
) { }
