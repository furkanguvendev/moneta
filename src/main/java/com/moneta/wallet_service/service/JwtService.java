package com.moneta.wallet_service.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;

public interface JwtService {
    String extractUsername(String token);
    List<String> extractRoles(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenValid(String token);
    String generateToken(String email);
    String generateToken(Map<String, Object> extraClaims, String email);
}