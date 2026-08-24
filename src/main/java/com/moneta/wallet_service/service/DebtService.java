package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.DebtPaymentRequest;
import com.moneta.wallet_service.dto.request.DebtRequest;
import com.moneta.wallet_service.dto.response.DebtResponse;
import com.moneta.wallet_service.enums.DebtType;

import java.util.List;

public interface DebtService {
    DebtResponse createDebt(Long userId, DebtRequest request);
    DebtResponse makePayment(Long debtId, DebtPaymentRequest request);
    List<DebtResponse> getDebtsByUserId(Long userId);
    List<DebtResponse> getDebtsByType(Long userId, DebtType debtType);
    List<DebtResponse> getActiveDebts(Long userId);
    DebtResponse getDebtById(Long debtId);
    void deleteDebt(Long debtId);
    void syncMonthlyInstallments(Long walletId, int year, int month);
    void syncAllDueInstallments();
}