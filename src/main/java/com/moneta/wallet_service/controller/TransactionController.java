package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions") // Base path eklendi
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long walletId) {
        return ResponseEntity.ok(transactionService.getTransactions(walletId));
    }

    @PostMapping("/wallet/{walletId}")
    public ResponseEntity<Transaction> addTransaction(
            @PathVariable Long walletId,
            @RequestBody Transaction transaction) {

        Transaction createdTransaction = transactionService.addTransaction(walletId, transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}