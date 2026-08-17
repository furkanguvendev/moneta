package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.dto.response.TransactionStatisticsResponse;
import com.moneta.wallet_service.entity.Category;
import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.exception.BaseException;
import com.moneta.wallet_service.exception.ResourceNotFoundException;
import com.moneta.wallet_service.mapper.TransactionMapper;
import com.moneta.wallet_service.repository.TransactionRepository;
import com.moneta.wallet_service.service.CategoryService;
import com.moneta.wallet_service.service.InvestmentSimulationService;
import com.moneta.wallet_service.service.TransactionService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final CategoryService categoryService;
    private final InvestmentSimulationService investmentSimulationService;
    private final TransactionMapper transactionMapper;

    @Override
    public TransactionResponse getTransactionById(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("İşlem bulunamadı! ID: " + transactionId));
        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse addTransaction(TransactionRequest request) {
        Wallet wallet = walletService.getWalletEntityById(request.walletId());
        Category category = categoryService.getCategoryEntityById(request.categoryId());
        TransactionType type = request.transactionType();

        if (type == TransactionType.EXPENSE && wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new BaseException("Yetersiz bakiye! Cüzdandaki miktar: " + wallet.getBalance(), HttpStatus.BAD_REQUEST);
        }

        BigDecimal impact = (type == TransactionType.INCOME) ? request.amount() : request.amount().negate();
        walletService.updateBalance(request.walletId(), impact);

        Transaction transaction = transactionMapper.toEntity(request);
        transaction.setWallet(wallet);
        transaction.setCategory(category);
        transaction.setTransactionDate(LocalDateTime.now());

        return transactionMapper.toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public List<TransactionResponse> addInstallmentTransaction(TransactionRequest request) {
        if (request.totalInstallment() == null || request.totalInstallment() <= 1) {
            return List.of(addTransaction(request));
        }

        Wallet wallet = walletService.getWalletEntityById(request.walletId());
        Category category = categoryService.getCategoryEntityById(request.categoryId());
        TransactionType type = request.transactionType();

        int count = request.totalInstallment();
        BigDecimal installmentAmount = request.amount().divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
        String groupKey = UUID.randomUUID().toString();
        LocalDateTime startDate = LocalDateTime.now();

        List<Transaction> createdTransactions = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Transaction tx = new Transaction();
            tx.setWallet(wallet);
            tx.setCategory(category);
            tx.setTransactionType(type);
            tx.setAmount(installmentAmount);
            tx.setDescription(request.description() + " (" + (i + 1) + "/" + count + " Taksit)");
            tx.setInstallmentGroupKey(groupKey);
            tx.setCurrentInstallment(i + 1);
            tx.setTotalInstallment(count);
            tx.setTransactionDate(startDate.plusMonths(i));

            createdTransactions.add(tx);
        }

        BigDecimal firstMonthImpact = (type == TransactionType.INCOME) ? installmentAmount : installmentAmount.negate();
        walletService.updateBalance(request.walletId(), firstMonthImpact);

        List<Transaction> saved = transactionRepository.saveAll(createdTransactions);
        return saved.stream().map(transactionMapper::toResponse).toList();
    }

    @Override
    public List<TransactionResponse> getTransactions(Long walletId) {
        List<Transaction> transactions = transactionRepository.findByWalletId(walletId);
        return transactions.stream().map(transactionMapper::toResponse).toList();
    }

    @Override
    public List<TransactionResponse> getCurrentBudgetPeriodTransactions(Long userId) {
        Wallet wallet = walletService.getWalletsByUserId(userId).stream().findFirst()
                .map(w -> walletService.getWalletEntityById(w.id()))
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcıya ait cüzdan bulunamadı."));

        User user = wallet.getUser();
        int budgetStartDay = user.getBudgetStartDay() != null ? user.getBudgetStartDay() : 1;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate;
        LocalDateTime endDate;

        if (now.getDayOfMonth() >= budgetStartDay) {
            startDate = now.withDayOfMonth(budgetStartDay).withHour(0).withMinute(0).withSecond(0);
            endDate = startDate.plusMonths(1).minusNanos(1);
        } else {
            startDate = now.minusMonths(1).withDayOfMonth(budgetStartDay).withHour(0).withMinute(0).withSecond(0);
            endDate = startDate.plusMonths(1).minusNanos(1);
        }

        List<Transaction> transactions = transactionRepository.findByUserIdAndTransactionDateBetween(userId, startDate, endDate);
        return transactions.stream().map(transactionMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek işlem bulunamadı! ID: " + transactionId));

        if (transaction.getInvestmentSimulationId() != null) {
            investmentSimulationService.deleteSimulationById(transaction.getInvestmentSimulationId());
            return;
        }

        BigDecimal reverseImpact = (transaction.getTransactionType() == TransactionType.INCOME)
                ? transaction.getAmount().negate()
                : transaction.getAmount();

        walletService.updateBalance(transaction.getWallet().getId(), reverseImpact);
        transactionRepository.delete(transaction);
    }

    @Override
    @Transactional
    public void deleteInstallmentGroup(String installmentGroupKey) {
        List<Transaction> groupTransactions = transactionRepository.findByInstallmentGroupKey(installmentGroupKey);
        if (!groupTransactions.isEmpty()) {
            transactionRepository.deleteAll(groupTransactions);
        }
    }

    @Override
    public List<TransactionStatisticsResponse> getExpenseStatistics(Long walletId) {
        List<Object[]> results = transactionRepository.getExpenceBreakdownByCategory(walletId);

        BigDecimal totalExpense = results.stream()
                .map(r -> (BigDecimal) r[2])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
                .map(result -> new TransactionStatisticsResponse(
                        (Long) result[0],
                        (String) result[1],
                        (BigDecimal) result[2],
                        calculatePercentage((BigDecimal) result[2], totalExpense)
                ))
                .toList();
    }

    private double calculatePercentage(BigDecimal amount, BigDecimal total) {
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0;
        return amount.divide(total, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue();
    }
}