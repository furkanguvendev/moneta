package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.repository.WalletRepository;
import com.moneta.wallet_service.service.UserService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;

    @Override
    public Wallet createWallet(Long userId, Wallet wallet) {
        User user = userService.getUserById(userId);
        wallet.setUser(user);
        return walletRepository.save(wallet);
    }

    @Override
    public void updateBalance(Long walletId, BigDecimal amount) {
        Wallet wallet = getWalletById(walletId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    public Wallet getWalletById(Long id) {
        return walletRepository.findById(id).orElseThrow(()-> new RuntimeException("Cüzdan Bulunamadı."));
    }
}
