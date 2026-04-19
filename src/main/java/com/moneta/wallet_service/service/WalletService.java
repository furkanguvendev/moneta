package com.moneta.wallet_service.service;

import com.moneta.wallet_service.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {

    Wallet createWallet(Long userId, Wallet wallet);
    void updateBalance(Long walletId, BigDecimal amount);
    Wallet getWalletById(Long id);
}
