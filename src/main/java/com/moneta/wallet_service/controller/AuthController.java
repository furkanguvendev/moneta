package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.LoginRequest;
import com.moneta.wallet_service.dto.response.AuthResponse;
import com.moneta.wallet_service.dto.response.LoginResponse;
import com.moneta.wallet_service.entity.Role;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.enums.RoleType;
import com.moneta.wallet_service.repository.UserRepository;
import com.moneta.wallet_service.service.JwtService;
import com.moneta.wallet_service.service.RoleService; // Yeni ekledik
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authBileti =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication onayliKullanici = authenticationManager.authenticate(authBileti);

        if (onayliKullanici.isAuthenticated()) {

            String token = jwtService.generateToken(loginRequest.getEmail());

            LoginResponse response = new LoginResponse(
                    onayliKullanici.getName(),
                    loginRequest.getEmail(),
                    token,
                    "Giriş başarıyla tamamlandı!"
            );

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody User register) {
        User user = new User();
        user.setUserName(register.getUserName());
        user.setEmail(register.getEmail());

        String encodedPassword = passwordEncoder.encode(register.getPassword());
        user.setPassword(encodedPassword);

        Role defaultRole = roleService.findByType(RoleType.USER);
        user.getRoles().add(defaultRole);

        userRepository.save(user);

        return new AuthResponse(
                user.getUserName(),
                user.getEmail(),
                encodedPassword
        );
    }
}