package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.dto.response.TransactionStatisticsResponse;
import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

    @GetMapping("/statistics/{walletId}")
    public ResponseEntity<List<TransactionStatisticsResponse>> getWalletStatistics(@PathVariable Long walletId) {
        List<TransactionStatisticsResponse> statistics = transactionService.getExpenseStatistics(walletId);
        return ResponseEntity.ok(statistics);
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