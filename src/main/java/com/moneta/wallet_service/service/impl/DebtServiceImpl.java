package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.DebtPaymentRequest;
import com.moneta.wallet_service.dto.request.DebtRequest;
import com.moneta.wallet_service.dto.response.DebtResponse;
import com.moneta.wallet_service.entity.Debt;
import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.DebtType;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.exception.BaseException;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.mapper.DebtMapper;
import com.moneta.wallet_service.repository.CategoryRepository;
import com.moneta.wallet_service.repository.DebtRepository;
import com.moneta.wallet_service.repository.TransactionRepository;
import com.moneta.wallet_service.service.DebtService;
import com.moneta.wallet_service.service.UserService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final UserService userService;
    private final WalletService walletService;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final DebtMapper debtMapper;

    @Override
    @Transactional
    public DebtResponse createDebt(Long userId, DebtRequest request) {
        User user = userService.getUserById(userId);

        Debt debt = debtMapper.toEntity(request);
        debt.setUser(user);

        return debtMapper.toResponse(debtRepository.save(debt));
    }

    @Override
    @Transactional
    public DebtResponse makePayment(Long debtId, DebtPaymentRequest request) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Borç kaydı bulunamadı! ID: " + debtId));

        if (debt.isCompleted()) {
            throw new BaseException("Bu borç/alacak zaten tamamen kapatılmış!", HttpStatus.BAD_REQUEST);
        }

        BigDecimal remaining = debt.getRemainingAmount();
        if (request.amount().compareTo(remaining) > 0) {
            throw new BaseException("Ödeme tutarı kalan tutardan fazla olamaz! Kalan: " + remaining, HttpStatus.BAD_REQUEST);
        }

        Wallet wallet = walletService.getWalletEntityById(request.walletId());

        TransactionType type = (debt.getDebtType() == DebtType.BORC) ? TransactionType.EXPENSE : TransactionType.INCOME;

        if (type == TransactionType.EXPENSE && wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new BaseException("Yetersiz bakiye!", HttpStatus.BAD_REQUEST);
        }

        BigDecimal impact = (type == TransactionType.INCOME) ? request.amount() : request.amount().negate();
        walletService.updateBalance(wallet.getId(), impact);

        debt.setRemainingAmount(debt.getRemainingAmount().subtract(request.amount()));
        if (debt.getRemainingAmount().compareTo(BigDecimal.ZERO) == 0) {
            debt.setCompleted(true);
        }

        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setAmount(request.amount());
        tx.setTransactionType(type);
        tx.setDescription((debt.getDebtType() == DebtType.BORC ? "Borç Ödemesi: " : "Alacak Tahsilatı: ") + debt.getTitle());
        tx.setTransactionDate(LocalDateTime.now());
        categoryRepository.findById(1L).ifPresent(tx::setCategory);

        transactionRepository.save(tx);

        return debtMapper.toResponse(debtRepository.save(debt));
    }

    @Override
    public List<DebtResponse> getDebtsByUserId(Long userId) {
        List<Debt> debts = debtRepository.findByUserId(userId);
        return debts.stream().map(debtMapper::toResponse).toList();
    }

    @Override
    public List<DebtResponse> getDebtsByType(Long userId, DebtType debtType) {
        List<Debt> debts = debtRepository.findByUserIdAndDebtType(userId, debtType);
        return debts.stream().map(debtMapper::toResponse).toList();
    }

    @Override
    public List<DebtResponse> getActiveDebts(Long userId) {
        List<Debt> debts = debtRepository.findByUserIdAndIsCompletedFalse(userId);
        return debts.stream().map(debtMapper::toResponse).toList();
    }

    @Override
    public DebtResponse getDebtById(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Borç kaydı bulunamadı! ID: " + debtId));
        return debtMapper.toResponse(debt);
    }

    @Override
    @Transactional
    public void deleteDebt(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek borç bulunamadı! ID: " + debtId));
        debtRepository.delete(debt);
    }
}