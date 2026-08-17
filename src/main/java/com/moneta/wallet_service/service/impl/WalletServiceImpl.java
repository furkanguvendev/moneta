package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.WalletRequest;
import com.moneta.wallet_service.dto.response.WalletResponse;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.mapper.WalletMapper;
import com.moneta.wallet_service.repository.WalletRepository;
import com.moneta.wallet_service.service.UserService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final UserService userService;
    private final WalletMapper walletMapper;

    @Override
    @Transactional
    public WalletResponse createWallet(Long userId, WalletRequest request) {
        User user = userService.getUserById(userId);

        Wallet wallet = walletMapper.toEntity(request);
        wallet.setUser(user);

        return walletMapper.toResponse(walletRepository.save(wallet));
    }

    @Override
    @Transactional
    public void updateBalance(Long walletId, BigDecimal amount) {
        Wallet wallet = getWalletEntityById(walletId);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
    }

    @Override
    public WalletResponse getWalletById(Long walletId) {
        Wallet wallet = getWalletEntityById(walletId);

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!wallet.getUser().getUserName().equals(currentUsername) && !wallet.getUser().getEmail().equals(currentUsername)) {
            throw new AccessDeniedException("Bu cüzdan bilgilerine erişim yetkiniz yok!");
        }

        return walletMapper.toResponse(wallet);
    }

    @Override
    public Wallet getWalletEntityById(Long id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cüzdan Bulunamadı. ID: " + id));
    }

    @Override
    public List<WalletResponse> getWalletsByUserId(Long userId) {
        List<Wallet> wallets = walletRepository.findAllByUserIdWithUser(userId);
        return wallets.stream().map(walletMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void updateBalanceAfterTransactionDelete(Long walletId, BigDecimal amount, TransactionType type) {
        Wallet wallet = getWalletEntityById(walletId);

        if (type == TransactionType.INCOME) {
            wallet.setBalance(wallet.getBalance().subtract(amount));
        } else if (type == TransactionType.EXPENSE) {
            wallet.setBalance(wallet.getBalance().add(amount));
        }

        walletRepository.save(wallet);
    }

    @Override
    @Transactional
    public void deleteWallet(Long walletId) {
        Wallet wallet = getWalletEntityById(walletId);
        walletRepository.delete(wallet);
    }
}