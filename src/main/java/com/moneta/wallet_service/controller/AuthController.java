package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest loginRequest){

        UsernamePasswordAuthenticationToken authBileti =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication  onayliKullanici = authenticationManager.authenticate(authBileti);

        if (onayliKullanici.isAuthenticated()) {
            return "Giriş Başarılı! Hoş geldin: " + onayliKullanici.getName();
        } else {
            return "Giriş başarısız.";
        }
    }
}
