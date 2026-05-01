package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.dto.response.TransactionStatisticsResponse;
import com.moneta.wallet_service.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    TransactionResponse getTransactionById(Long transactionId);
    TransactionResponse addTransaction(TransactionRequest request);
    List<TransactionResponse> getTransactions(Long walletId);
    void deleteTransaction(Long transactionId);
    List<TransactionStatisticsResponse> getExpenseStatistics(Long walletId);
}