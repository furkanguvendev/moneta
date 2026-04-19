package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/wallet/{walletId}")
    public ResponseEntity<Transaction> addTransaction(
            @PathVariable Long walletId,
            @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.addTransaction(walletId, transaction));
    }
}
