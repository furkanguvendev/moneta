package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.dto.response.MonthlyBreakdownResponse;
import com.moneta.wallet_service.dto.response.MonthlySummaryResponse;
import com.moneta.wallet_service.entity.User;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.repository.TransactionRepository;
import com.moneta.wallet_service.service.AnalyticsService;
import com.moneta.wallet_service.service.UserService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final WalletService walletService;

    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.getUserByUsernameOrEmail(email);
    }

    private void verifyWalletOwnership(Long walletId, User authUser) {
        Wallet wallet = walletService.getWalletEntityById(walletId);
        if (!wallet.getUser().getId().equals(authUser.getId())) {
            throw new AccessDeniedException("Bu cüzdana erişim yetkiniz yok!");
        }
    }

    @Override
    public MonthlySummaryResponse getMonthlySummary(Long userId) {
        User authUser = getAuthenticatedUser();

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        MonthlySummaryResponse summary = transactionRepository.findMonthlySummaryByUserIdAndMonthAndYear(authUser.getId(), currentMonth, currentYear);
        return summary != null ? summary : new MonthlySummaryResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public MonthlySummaryResponse getCurrentBudgetPeriodSummary(Long userId) {
        User authUser = getAuthenticatedUser();
        int budgetStartDay = authUser.getBudgetStartDay() != null ? authUser.getBudgetStartDay() : 1;

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

        var transactions = transactionRepository.findByUserIdAndTransactionDateBetween(authUser.getId(), startDate, endDate);

        BigDecimal income = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.INCOME)
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expense = transactions.stream()
                .filter(t -> t.getTransactionType() == TransactionType.EXPENSE)
                .map(t -> t.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new MonthlySummaryResponse(income, expense, income.subtract(expense));
    }

    @Override
    public MonthlySummaryResponse getWalletMonthlySummary(Long walletId) {
        User authUser = getAuthenticatedUser();
        verifyWalletOwnership(walletId, authUser);

        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();

        BigDecimal totalIncome = transactionRepository.findMonthlyTotalByWalletIdAndType(
                walletId, TransactionType.INCOME, currentMonth, currentYear
        );

        BigDecimal totalExpense = transactionRepository.findMonthlyTotalByWalletIdAndType(
                walletId, TransactionType.EXPENSE, currentMonth, currentYear
        );

        BigDecimal income = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        BigDecimal expense = totalExpense != null ? totalExpense : BigDecimal.ZERO;

        return new MonthlySummaryResponse(income, expense, income.subtract(expense));
    }

    @Override
    public List<MonthlyBreakdownResponse> getWalletMonthlyBreakdown(Long walletId) {
        User authUser = getAuthenticatedUser();
        verifyWalletOwnership(walletId, authUser);

        return transactionRepository.findMonthlyBreakdownByWalletId(walletId);
    }
}