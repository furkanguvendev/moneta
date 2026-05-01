package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.dto.response.TransactionStatisticsResponse;
import com.moneta.wallet_service.entity.Category;
import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.exception.BaseException;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.repository.CategoryRepository;
import com.moneta.wallet_service.repository.TransactionRepository;
import com.moneta.wallet_service.service.TransactionService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final CategoryRepository categoryRepository;

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("İşlem bulunamadı! ID: " + transactionId));
        return convertToResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse addTransaction(TransactionRequest request) {
        Wallet wallet = walletService.getWalletEntityById(request.walletId());
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori bulunamadı! ID: " + request.categoryId()));

        TransactionType type = TransactionType.valueOf(request.transactionType());

        if (type == TransactionType.EXPENSE && wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new BaseException("Yetersiz bakiye! Cüzdandaki miktar: " + wallet.getBalance(), HttpStatus.BAD_REQUEST);
        }

        BigDecimal impact = (type == TransactionType.INCOME) ? request.amount() : request.amount().negate();
        walletService.updateBalance(request.walletId(), impact);

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setTransactionType(type);
        transaction.setWallet(wallet);
        transaction.setCategory(category);

        return convertToResponse(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionResponse> getTransactions(Long walletId) {
        return transactionRepository.findByWalletId(walletId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek işlem bulunamadı! ID: " + transactionId));

        BigDecimal reverseImpact = (transaction.getTransactionType() == TransactionType.INCOME)
                ? transaction.getAmount().negate()
                : transaction.getAmount();

        walletService.updateBalance(transaction.getWallet().getId(), reverseImpact);
        transactionRepository.delete(transaction);
    }

    @Override
    public List<TransactionStatisticsResponse> getExpenseStatistics(Long walletId) {
        List<Object[]> results = transactionRepository.getExpenceBreakdownByCategory(walletId);

        BigDecimal totalExpense = results.stream()
                .map(r -> (BigDecimal) r[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
                .map(result -> new TransactionStatisticsResponse(
                        (String) result[0],
                        (BigDecimal) result[1],
                        calculatePercentage((BigDecimal) result[1], totalExpense)
                ))
                .toList();
    }

    private double calculatePercentage(BigDecimal amount, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0;
        return amount.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue();
    }

    private TransactionResponse convertToResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getDescription(),
                transaction.getCategory().getName(),
                transaction.getCategory().isMandatory(),
                transaction.getWallet().getName(),
                transaction.getTransactionType().toString(),
                transaction.getTransactionDate()
        );
    }
}