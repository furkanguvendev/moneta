package com.moneta.wallet_service.service;

import com.moneta.wallet_service.dto.request.WalletRequest;
import com.moneta.wallet_service.dto.response.WalletResponse;
import com.moneta.wallet_service.entity.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    WalletResponse createWallet(Long userId, WalletRequest request);
    void updateBalance(Long walletId, BigDecimal amount);
    WalletResponse getWalletById(Long id);
    Wallet getWalletEntityById(Long id);
}
