package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.LoginRequest;
import com.moneta.wallet_service.dto.response.AuthResponse;
import com.moneta.wallet_service.dto.response.LoginResponse;
import com.moneta.wallet_service.entity.Role;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.enums.RoleType;
import com.moneta.wallet_service.service.JwtService;
import com.moneta.wallet_service.service.RoleService;
import com.moneta.wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken authBileti =
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication onayliKullanici = authenticationManager.authenticate(authBileti);

        if (onayliKullanici.isAuthenticated()) {
            User user = userService.getUserByUsernameOrEmailWithRoles(loginRequest.getEmail());

            Map<String, Object> extraClaims = new HashMap<>();
            List<String> roles = user.getRoles().stream()
                    .map(role -> role.getRoleType().name())
                    .toList();
            extraClaims.put("roles", roles);

            String token = jwtService.generateToken(extraClaims, loginRequest.getEmail());

            LoginResponse response = new LoginResponse(
                    user.getId(),
                    user.getUserName(),
                    user.getEmail(),
                    token,
                    "Giriş başarıyla tamamlandı!"
            );

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User register) {
        User user = new User();
        user.setUserName(register.getUserName());
        user.setEmail(register.getEmail());

        String encodedPassword = passwordEncoder.encode(register.getPassword());
        user.setPassword(encodedPassword);

        Role defaultRole = roleService.findByType(RoleType.USER);
        user.getRoles().add(defaultRole);

        User savedUser = userService.saveUser(user);

        AuthResponse response = new AuthResponse(
                savedUser.getUserName(),
                savedUser.getEmail(),
                "Kayıt işlemi başarıyla tamamlandı."
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}