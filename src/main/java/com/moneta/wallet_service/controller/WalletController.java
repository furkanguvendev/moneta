package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{walletId}")
    public ResponseEntity<Wallet> getWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<Wallet> createWallet(@PathVariable Long userId, @RequestBody Wallet wallet) {
        return ResponseEntity.ok(walletService.createWallet(userId, wallet));
    }
}
