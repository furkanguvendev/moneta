package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.entity.Transaction;
import java.util.List;

public interface TransactionService {
    TransactionResponse getTransactionById(Long transactionId);
    TransactionResponse addTransaction(TransactionRequest request);
    List<TransactionResponse> getTransactions(Long walletId);
    void deleteTransaction(Long transactionId);
}