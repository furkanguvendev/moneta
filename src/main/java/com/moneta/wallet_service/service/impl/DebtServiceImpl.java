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
import java.math.RoundingMode;
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

        // Aylık taksit tutarını hesapla
        int installments = request.totalInstallments() != null && request.totalInstallments() > 0
                ? request.totalInstallments() : 1;

        debt.setTotalInstallments(installments);

        BigDecimal monthly = request.totalAmount().divide(
                BigDecimal.valueOf(installments), 2, RoundingMode.HALF_UP
        );
        debt.setMonthlyInstallment(monthly);

        return debtMapper.toResponse(debtRepository.save(debt));
    }

    @Override
    @Transactional
    public DebtResponse makePayment(Long debtId, DebtPaymentRequest request) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Kredi/Taksit kaydı bulunamadı! ID: " + debtId));

        if (debt.isCompleted()) {
            throw new BaseException("Bu kredi/taksit zaten tamamen ödenmiş!", HttpStatus.BAD_REQUEST);
        }

        BigDecimal paymentAmount = request.amount() != null ? request.amount() : debt.getMonthlyInstallment();

        if (paymentAmount.compareTo(debt.getRemainingAmount()) > 0) {
            paymentAmount = debt.getRemainingAmount();
        }

        Wallet wallet = walletService.getWalletEntityById(request.walletId());

        if (wallet.getBalance().compareTo(paymentAmount) < 0) {
            throw new BaseException("Yetersiz bakiye!", HttpStatus.BAD_REQUEST);
        }

        walletService.updateBalance(wallet.getId(), paymentAmount.negate());

        debt.setRemainingAmount(debt.getRemainingAmount().subtract(paymentAmount));
        debt.setPaidInstallments(debt.getPaidInstallments() + 1);

        if (debt.getRemainingAmount().compareTo(BigDecimal.ZERO) <= 0
                || debt.getPaidInstallments() >= debt.getTotalInstallments()) {
            debt.setRemainingAmount(BigDecimal.ZERO);
            debt.setCompleted(true);
        }

        Transaction tx = new Transaction();
        tx.setWallet(wallet);
        tx.setAmount(paymentAmount);
        tx.setTransactionType(TransactionType.EXPENSE);
        tx.setDescription("Kredi/Taksit Ödemesi: " + debt.getTitle() +
                " (" + debt.getPaidInstallments() + "/" + debt.getTotalInstallments() + ")");
        tx.setTransactionDate(LocalDateTime.now());

        if (request.categoryId() != null) {
            categoryRepository.findById(request.categoryId()).ifPresent(tx::setCategory);
        }

        transactionRepository.save(tx);

        return debtMapper.toResponse(debtRepository.save(debt));
    }

    @Override
    public List<DebtResponse> getDebtsByUserId(Long userId) {
        return debtRepository.findByUserId(userId).stream().map(debtMapper::toResponse).toList();
    }

    @Override
    public List<DebtResponse> getDebtsByType(Long userId, DebtType debtType) {
        return debtRepository.findByUserIdAndDebtType(userId, debtType).stream().map(debtMapper::toResponse).toList();
    }

    @Override
    public List<DebtResponse> getActiveDebts(Long userId) {
        return debtRepository.findByUserIdAndIsCompletedFalse(userId).stream().map(debtMapper::toResponse).toList();
    }

    @Override
    public DebtResponse getDebtById(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Kredi kaydı bulunamadı! ID: " + debtId));
        return debtMapper.toResponse(debt);
    }

    @Override
    @Transactional
    public void deleteDebt(Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek kayıt bulunamadı! ID: " + debtId));
        debtRepository.delete(debt);
    }
}