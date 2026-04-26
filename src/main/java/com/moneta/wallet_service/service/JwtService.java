package com.moneta.wallet_service.service;

public interface JwtService {
    String generateToken(String email);
}