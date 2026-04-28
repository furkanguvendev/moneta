package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.WalletRequest;
import com.moneta.wallet_service.dto.response.WalletResponse;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
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
    public WalletResponse createWallet(Long userId, WalletRequest request) {
        User user = userService.getUserById(userId);

        Wallet wallet = new Wallet();
        wallet.setName(request.name());
        wallet.setBalance(request.balance());
        wallet.setCurrency(request.currency());
        wallet.setUser(user);

        return convertToResponse(walletRepository.save(wallet));
    }

    @Override
    public void updateBalance(Long walletId, BigDecimal amount) {
        Wallet wallet = getWalletEntityById(walletId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    public WalletResponse getWalletById(Long id) {
        return convertToResponse(getWalletEntityById(id));
    }

    @Override
    public Wallet getWalletEntityById(Long id) {
        // RuntimeException -> ResourceNotFoundException (404)
        return walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cüzdan Bulunamadı. ID: " + id));
    }

    private WalletResponse convertToResponse(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getName(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getUser().getUserName()
        );
    }
}