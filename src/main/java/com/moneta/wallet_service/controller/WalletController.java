package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.WalletRequest;
import com.moneta.wallet_service.dto.response.WalletResponse;
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
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long walletId) {
        return ResponseEntity.ok(walletService.getWalletById(walletId));
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<WalletResponse> createWallet(
            @PathVariable Long userId,
            @RequestBody WalletRequest request) {
        return ResponseEntity.ok(walletService.createWallet(userId, request));
    }
}
