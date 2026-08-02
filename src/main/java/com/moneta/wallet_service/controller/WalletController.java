package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.WalletRequest;
import com.moneta.wallet_service.dto.response.WalletResponse;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/detail/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletResponse>> getUserWallets(@PathVariable Long userId) {
        return ResponseEntity.ok(walletService.getWalletsByUserId(userId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<WalletResponse> createWallet(
            @PathVariable Long userId,
            @RequestBody WalletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(walletService.createWallet(userId, request));
    }

    @DeleteMapping("/{walletId}")
    public ResponseEntity<Void> deleteWallet(@PathVariable Long walletId) {
        walletService.deleteWallet(walletId);
        return ResponseEntity.noContent().build();
    }
}