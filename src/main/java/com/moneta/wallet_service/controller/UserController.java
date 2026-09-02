package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.UserUpdateRequest;
import com.moneta.wallet_service.dto.response.UserResponse;
import com.moneta.wallet_service.exception.BaseException;
import com.moneta.wallet_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        verifyOwnership(id);
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        verifyOwnership(id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private void verifyOwnership(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new BaseException("Yetkilendirme bulunamadı.", HttpStatus.UNAUTHORIZED);
        }

        String authenticatedEmail = authentication.getName();
        String targetUserEmail = userService.getUserById(id).getEmail();

        if (!authenticatedEmail.equalsIgnoreCase(targetUserEmail)) {
            throw new BaseException("Bu işlem için yetkiniz yok.", HttpStatus.FORBIDDEN);
        }
    }
}