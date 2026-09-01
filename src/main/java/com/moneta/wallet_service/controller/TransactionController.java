package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.request.TransactionUpdateRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.dto.response.TransactionStatisticsResponse;
import com.moneta.wallet_service.service.TransactionService;
import jakarta.validation.Valid;
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

    @GetMapping("/statistics/{walletId}")
    public ResponseEntity<List<TransactionStatisticsResponse>> getWalletStatistics(@PathVariable Long walletId) {
        return ResponseEntity.ok(transactionService.getExpenseStatistics(walletId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request) {
        return ResponseEntity.ok(transactionService.updateTransaction(id, request));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addTransaction(request));
    }

    @PostMapping("/installment")
    public ResponseEntity<List<TransactionResponse>> addInstallmentTransaction(@Valid @RequestBody TransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addInstallmentTransaction(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/installment/{groupKey}")
    public ResponseEntity<Void> deleteInstallmentGroup(@PathVariable String groupKey) {
        transactionService.deleteInstallmentGroup(groupKey);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/wallet/{walletId}/month/{year}/{month}")
    public ResponseEntity<Void> deleteTransactionsByMonth(
            @PathVariable Long walletId,
            @PathVariable int year,
            @PathVariable int month) {
        transactionService.deleteTransactionsByMonth(walletId, year, month);
        return ResponseEntity.noContent().build();
    }
}