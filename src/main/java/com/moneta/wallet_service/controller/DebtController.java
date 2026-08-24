package com.moneta.wallet_service.controller;

import com.moneta.wallet_service.dto.request.DebtPaymentRequest;
import com.moneta.wallet_service.dto.request.DebtRequest;
import com.moneta.wallet_service.dto.response.DebtResponse;
import com.moneta.wallet_service.enums.DebtType;
import com.moneta.wallet_service.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<DebtResponse> createDebt(
            @PathVariable Long userId,
            @RequestBody DebtRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(debtService.createDebt(userId, request));
    }

    @PostMapping("/{debtId}/pay")
    public ResponseEntity<DebtResponse> makePayment(
            @PathVariable Long debtId,
            @RequestBody DebtPaymentRequest request) {
        return ResponseEntity.ok(debtService.makePayment(debtId, request));
    }

    @PostMapping("/wallet/{walletId}/sync")
    public ResponseEntity<Void> syncInstallments(
            @PathVariable Long walletId,
            @RequestParam int year,
            @RequestParam int month) {
        debtService.syncMonthlyInstallments(walletId, year, month);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DebtResponse>> getDebtsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(debtService.getDebtsByUserId(userId));
    }

    @GetMapping("/user/{userId}/type/{debtType}")
    public ResponseEntity<List<DebtResponse>> getDebtsByType(
            @PathVariable Long userId,
            @PathVariable DebtType debtType) {
        return ResponseEntity.ok(debtService.getDebtsByType(userId, debtType));
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<DebtResponse>> getActiveDebts(@PathVariable Long userId) {
        return ResponseEntity.ok(debtService.getActiveDebts(userId));
    }

    @GetMapping("/{debtId}")
    public ResponseEntity<DebtResponse> getDebtById(@PathVariable Long debtId) {
        return ResponseEntity.ok(debtService.getDebtById(debtId));
    }

    @DeleteMapping("/{debtId}")
    public ResponseEntity<Void> deleteDebt(@PathVariable Long debtId) {
        debtService.deleteDebt(debtId);
        return ResponseEntity.noContent().build();
    }
}