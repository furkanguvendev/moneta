package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.dto.response.TransactionStatisticsResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse getTransactionById(Long transactionId);

    TransactionResponse addTransaction(TransactionRequest request);

    List<TransactionResponse> addInstallmentTransaction(TransactionRequest request);

    List<TransactionResponse> getTransactions(Long walletId);

    List<TransactionResponse> getCurrentBudgetPeriodTransactions(Long userId);

    void deleteTransaction(Long transactionId);

    void deleteInstallmentGroup(String installmentGroupKey);

    List<TransactionStatisticsResponse> getExpenseStatistics(Long walletId);
}