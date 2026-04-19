package com.moneta.wallet_service.service;

import com.moneta.wallet_service.entity.Transaction;

public interface TransactionService {

    Transaction addTransaction(Long walletId, Transaction transaction);
}
