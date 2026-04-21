package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long walletId) {
        List<Transaction> transactions = transactionService.getTransactions(walletId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/wallet/{walletId}")
    public ResponseEntity<Transaction> addTransaction(
            @PathVariable Long walletId,
            @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.addTransaction(walletId, transaction));
    }

    @DeleteMapping("/wallet/{transactionId}")
    public void deleteTransaction(@PathVariable Long transactionId) {
        transactionService.deleteTransactions(transactionId);
    }
}
