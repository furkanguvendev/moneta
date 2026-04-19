package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long id) {
        return ResponseEntity.ok(walletService.getWalletById(id));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Wallet> createWallet(@PathVariable Long userId, @RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.createWallet(userId, wallet));
    }
}
