package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionResponse>> getTransactions(@PathVariable Long walletId) {
        return ResponseEntity.ok(transactionService.getTransactions(walletId));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(@RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addTransaction(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}