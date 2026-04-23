package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("#userId == authentication.principal.id")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

}
