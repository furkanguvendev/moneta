package com.moneta.wallet_service.service;

import com.moneta.wallet_service.entity.Transaction;

import java.util.List;

public interface TransactionService {

    Transaction addTransaction(Long walletId, Transaction transaction);

    List<Transaction> getTransactions(Long walletId);

    void deleteTransactions(Long transactionId);
}
